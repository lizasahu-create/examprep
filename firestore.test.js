const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- UNAUTHENTICATED ACCESS TESTS ---

test("Unauthenticated user: cannot read or write any user profile, attempts, or progress", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      themeMode: "PAPER",
      createdAt: past,
      updatedAt: past,
    })
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("exam_attempts").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("set_progress").get()
  );
});

// --- USER PROFILE TESTS ---

test("Authenticated owner: valid UserProfile create & partial themeMode update succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@example.com",
      themeMode: "PAPER",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Partial themeMode update preserving immutable fields
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      themeMode: "DARK",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject invalid themeMode enum
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      themeMode: "NEON",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field (Shadow Update Test)
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Authenticated user: cannot read, list, or modify another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const past = new Date(Date.now() - 10000);
    await ctx.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob",
      email: "bob@example.com",
      themeMode: "PAPER",
      createdAt: past,
      updatedAt: past,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      themeMode: "DARK",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

// --- EXAM ATTEMPTS TESTS ---

test("Authenticated owner: valid ExamAttempt create & list succeed; update & bad totals fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const nowPast = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_attempts")
      .doc("attempt_1")
      .set({
        userId: ALICE_UID,
        setId: "drive_file_123",
        setHeading: "Mock Test 01 - General Studies",
        category: "PRACTICE",
        totalQuestions: 20,
        correctCount: 15,
        incorrectCount: 3,
        skippedCount: 2,
        timeTakenSeconds: 780,
        createdAt: nowPast,
      })
  );

  // List own exam_attempts
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("exam_attempts").get()
  );

  // Immutable once submitted
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_attempts")
      .doc("attempt_1")
      .update({ correctCount: 20 })
  );

  // Reject mismatched count sum
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_attempts")
      .doc("attempt_bad")
      .set({
        userId: ALICE_UID,
        setId: "drive_file_123",
        setHeading: "Bad Sum",
        category: "PRACTICE",
        totalQuestions: 20,
        correctCount: 19,
        incorrectCount: 5,
        skippedCount: 0,
        timeTakenSeconds: 100,
        createdAt: nowPast,
      })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_attempts")
      .doc("attempt_future")
      .set({
        userId: ALICE_UID,
        setId: "drive_file_123",
        setHeading: "Future Time",
        category: "PRACTICE",
        totalQuestions: 10,
        correctCount: 10,
        incorrectCount: 0,
        skippedCount: 0,
        timeTakenSeconds: 100,
        createdAt: new Date(Date.now() + 60000),
      })
  );

  // Reject @DocumentId 'id' inside write payload
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_attempts")
      .doc("attempt_with_id")
      .set({
        id: "attempt_with_id",
        userId: ALICE_UID,
        setId: "drive_file_123",
        setHeading: "With ID",
        category: "PRACTICE",
        totalQuestions: 10,
        correctCount: 10,
        incorrectCount: 0,
        skippedCount: 0,
        timeTakenSeconds: 100,
        createdAt: nowPast,
      })
  );
});

// --- SET PROGRESS TESTS ---

test("Authenticated owner: valid SetProgress create, partial update, and cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("set_progress")
      .doc("pdf_math_1")
      .set({
        userId: ALICE_UID,
        setHeading: "Mathematics Set 1",
        category: "SECTIONAL",
        answeredCount: 5,
        correctCount: 4,
        totalQuestions: 25,
        createdAt: created,
        updatedAt: created,
      })
  );

  // Partial update
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("set_progress")
      .doc("pdf_math_1")
      .update({
        answeredCount: 10,
        correctCount: 8,
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // List own progress
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("set_progress").get()
  );

  // Cross-user denial
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("set_progress").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("set_progress")
      .doc("pdf_math_1")
      .update({ answeredCount: 25, updatedAt: new Date(Date.now() - 1000) })
  );
});
