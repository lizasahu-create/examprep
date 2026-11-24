# R8 / ProGuard optimization rules for ExamPrep (com.examprep.apply)
# Keeps reflection-backed Firestore & Credential Manager models while maximizing DEX obfuscation.

# Retain line numbers and source filenames so Play Console can deobfuscate stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve @Keep annotated Firestore document classes and their fields/constructors
-keep @androidx.annotation.Keep class * {
    <init>(...);
    <fields>;
    <methods>;
}

# Preserve Firestore property annotations used during toObject() deserialization
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

# Strip verbose and debug logs in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}
