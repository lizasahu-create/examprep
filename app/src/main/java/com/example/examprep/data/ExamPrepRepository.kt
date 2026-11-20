package com.example.examprep.data

import com.example.examprep.model.*

object ExamPrepRepository {

    private val gkQuestions = listOf(
        Question(
            id = "gk_1",
            subject = "General Knowledge",
            questionText = "Which planet is known as the Red Planet?",
            options = listOf("Earth", "Mars", "Jupiter", "Venus"),
            correctIndex = 1,
            explanation = "Mars is called the Red Planet because its surface contains high amounts of iron oxide (rust), giving it a reddish hue."
        ),
        Question(
            id = "gk_2",
            subject = "General Knowledge",
            questionText = "What is the capital of India?",
            options = listOf("Mumbai", "New Delhi", "Kolkata", "Chennai"),
            correctIndex = 1,
            explanation = "New Delhi is the official national capital of India, established during British rule and confirmed upon independence."
        ),
        Question(
            id = "gk_3",
            subject = "General Knowledge",
            questionText = "Who is known as the 'Father of the Indian Constitution'?",
            options = listOf("Mahatma Gandhi", "Dr. B.R. Ambedkar", "Jawaharlal Nehru", "Sardar Patel"),
            correctIndex = 1,
            explanation = "Dr. Bhimrao Ramji Ambedkar chaired the Drafting Committee of the Constituent Assembly and played a pivotal role in drafting the Constitution."
        ),
        Question(
            id = "gk_4",
            subject = "General Knowledge",
            questionText = "The famous Konark Sun Temple is located in which state of India?",
            options = listOf("Odisha", "West Bengal", "Bihar", "Tamil Nadu"),
            correctIndex = 0,
            explanation = "Konark Sun Temple is a 13th-century CE Sun temple at Konark about 35 kilometres northeast from Puri on the coastline of Odisha."
        ),
        Question(
            id = "gk_5",
            subject = "General Knowledge",
            questionText = "Which river is often known as the 'Sorrow of Bihar'?",
            options = listOf("Ganga", "Yamuna", "Kosi", "Gandak"),
            correctIndex = 2,
            explanation = "The Kosi River is known as the Sorrow of Bihar because its unpredictable flooding causes immense damage to lives and property."
        ),
        Question(
            id = "gk_6",
            subject = "General Knowledge",
            questionText = "Which is the largest ocean in the world?",
            options = listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"),
            correctIndex = 2,
            explanation = "The Pacific Ocean covers more than 30% of the Earth's surface area, making it larger than all land masses combined."
        ),
        Question(
            id = "gk_7",
            subject = "General Knowledge",
            questionText = "In which year did India gain independence from British rule?",
            options = listOf("1945", "1947", "1950", "1952"),
            correctIndex = 1,
            explanation = "India achieved independence at midnight on 15 August 1947."
        ),
        Question(
            id = "gk_8",
            subject = "General Knowledge",
            questionText = "Which Indian state has the longest coastline?",
            options = listOf("Maharashtra", "Gujarat", "Andhra Pradesh", "Tamil Nadu"),
            correctIndex = 1,
            explanation = "Gujarat boasts India's longest mainland coastline, stretching over 1,600 kilometers along the Arabian Sea."
        )
    )

    private val mathQuestions = listOf(
        Question(
            id = "m_1",
            subject = "Mathematics",
            questionText = "What is the value of √16?",
            options = listOf("2", "4", "6", "8"),
            correctIndex = 1,
            explanation = "√16 = 4 because 4 × 4 = 16."
        ),
        Question(
            id = "m_2",
            subject = "Mathematics",
            questionText = "If x + 3 = 7, then what is the value of x?",
            options = listOf("2", "3", "4", "5"),
            correctIndex = 2,
            explanation = "Subtracting 3 from both sides gives x = 7 - 3 = 4."
        ),
        Question(
            id = "m_3",
            subject = "Mathematics",
            questionText = "The area of a circle with radius r is given by which formula?",
            options = listOf("2πr", "πr²", "2πr²", "πr"),
            correctIndex = 1,
            explanation = "Area of circle = πr², while 2πr gives the circumference."
        ),
        Question(
            id = "m_4",
            subject = "Mathematics",
            questionText = "Find the simple interest on ₹1000 at 5% per annum for 2 years.",
            options = listOf("₹50", "₹100", "₹150", "₹200"),
            correctIndex = 1,
            explanation = "Simple Interest = (P × R × T) / 100 = (1000 × 5 × 2) / 100 = ₹100."
        ),
        Question(
            id = "m_5",
            subject = "Mathematics",
            questionText = "What is the average of 10, 20, 30, 40, and 50?",
            options = listOf("25", "30", "35", "40"),
            correctIndex = 1,
            explanation = "Sum = 150, Total elements = 5. Average = 150 / 5 = 30."
        )
    )

    private val reasoningQuestions = listOf(
        Question(
            id = "r_1",
            subject = "Reasoning",
            questionText = "If CAT = 24 and DOG = 26, then what is RAT?",
            options = listOf("39", "40", "42", "45"),
            correctIndex = 0,
            explanation = "R(18) + A(1) + T(20) = 39."
        ),
        Question(
            id = "r_2",
            subject = "Reasoning",
            questionText = "Which number replaces the '?' in the series: 2, 6, 12, 20, 30, ?",
            options = listOf("38", "40", "42", "44"),
            correctIndex = 2,
            explanation = "Differences are +4, +6, +8, +10, +12. So 30 + 12 = 42."
        ),
        Question(
            id = "r_3",
            subject = "Reasoning",
            questionText = "Pointing to a man, a woman said, 'His mother is the only daughter of my mother.' How is the woman related to the man?",
            options = listOf("Sister", "Mother", "Aunt", "Grandmother"),
            correctIndex = 1,
            explanation = "'Only daughter of my mother' is the woman herself. Hence she is his mother."
        )
    )

    private val computerQuestions = listOf(
        Question(
            id = "c_1",
            subject = "Computer",
            questionText = "Which unit of CPU performs mathematical calculations?",
            options = listOf("Control Unit", "ALU", "Register", "Cache"),
            correctIndex = 1,
            explanation = "ALU (Arithmetic Logic Unit) executes arithmetic (+, -, *, /) and logic operations."
        ),
        Question(
            id = "c_2",
            subject = "Computer",
            questionText = "What does RAM stand for?",
            options = listOf("Random Access Memory", "Read Access Memory", "Run Auto Memory", "Real Action Memory"),
            correctIndex = 0,
            explanation = "RAM stands for Random Access Memory, the primary volatile memory of a computer."
        )
    )

    private val odishaGkQuestions = listOf(
        Question(
            id = "ogk_1",
            subject = "Odisha GK",
            questionText = "Which lake in Odisha is the largest brackish water lagoon in Asia?",
            options = listOf("Ansupa Lake", "Chilika Lake", "Kanjia Lake", "Sara Lake"),
            correctIndex = 1,
            explanation = "Chilika Lake is Asia's largest brackish water lagoon and a renowned Ramsar wetland site."
        ),
        Question(
            id = "ogk_2",
            subject = "Odisha GK",
            questionText = "Who wrote the famous Odia epic 'Dandi Ramayana'?",
            options = listOf("Sarala Das", "Balarama Dasa", "Jagannatha Dasa", "Upendra Bhanja"),
            correctIndex = 1,
            explanation = "Balarama Dasa, one of the Panchasakhas, authored Jagamohana Ramayana (Dandi Ramayana)."
        )
    )

    // Practice Sets List
    val practiceSets: MutableList<PracticeSet> = mutableListOf(
        PracticeSet(
            id = "set_gk_1",
            title = "General Knowledge Set 1",
            category = "General Knowledge",
            totalQuestions = 8,
            durationMinutes = 10,
            questions = gkQuestions
        ),
        PracticeSet(
            id = "set_math_1",
            title = "Mathematics Set 1",
            category = "Quantitative Aptitude",
            totalQuestions = 5,
            durationMinutes = 15,
            questions = mathQuestions
        ),
        PracticeSet(
            id = "set_reasoning_1",
            title = "Reasoning Set 1",
            category = "Logical Reasoning",
            totalQuestions = 3,
            durationMinutes = 8,
            questions = reasoningQuestions
        ),
        PracticeSet(
            id = "set_computer_1",
            title = "Computer Set 1",
            category = "Information Tech",
            totalQuestions = 2,
            durationMinutes = 5,
            questions = computerQuestions
        ),
        PracticeSet(
            id = "set_odisha_1",
            title = "Odisha GK Set 1",
            category = "State GK & Heritage",
            totalQuestions = 2,
            durationMinutes = 5,
            questions = odishaGkQuestions
        )
    )

    // 11 Sectional subjects matching Mockup #10
    val sectionalSubjects: List<SectionalSubject> = listOf(
        SectionalSubject(
            id = "math",
            name = "Mathematics",
            pagesCount = 25,
            description = "Algebra, Arithmetic, Geometry, Mensuration & Number Systems",
            topics = listOf("Number Systems", "Percentages & Profit-Loss", "Simple & Compound Interest", "Geometry & Mensuration"),
            studyNotes = listOf(
                StudyNote(
                    id = "math_n1",
                    title = "Key Algebraic Identities",
                    content = "Master these foundational formulas for rapid calculation in competitive examinations.",
                    formulasOrHighlights = listOf(
                        "(a + b)² = a² + 2ab + b²",
                        "(a - b)² = a² - 2ab + b²",
                        "a² - b² = (a + b)(a - b)",
                        "(a + b + c)² = a² + b² + c² + 2(ab + bc + ca)"
                    ),
                    practiceQuestion = "If a + b = 9 and ab = 20, find a² + b².",
                    practiceAnswer = "Solution: a² + b² = (a + b)² - 2ab = 81 - 40 = 41."
                ),
                StudyNote(
                    id = "math_n2",
                    title = "Circle & Sphere Mensuration",
                    content = "Essential 2D and 3D geometric properties for area and volume questions.",
                    formulasOrHighlights = listOf(
                        "Area of circle = πr²",
                        "Circumference of circle = 2πr",
                        "Volume of sphere = (4/3)πr³",
                        "Surface area of sphere = 4πr²"
                    ),
                    practiceQuestion = "Find the volume of a sphere of radius 7 cm (take π = 22/7).",
                    practiceAnswer = "Solution: V = (4/3) × (22/7) × 7 × 7 × 7 = 1437.33 cm³."
                )
            )
        ),
        SectionalSubject(
            id = "odia",
            name = "Odia",
            pagesCount = 20,
            description = "Odia Byakarana, Sandhi, Samasa, Rudhi & Sahitya history",
            topics = listOf("Odia Bhasa & Lipi", "Sandhi Prakarana", "Rudhi & Lokabani", "Bichar & Sahitya"),
            studyNotes = listOf(
                StudyNote(
                    id = "odia_n1",
                    title = "ପ୍ରମୁଖ ରୂଢ଼ି ଓ ଲୋକବାଣୀ (Important Idioms)",
                    content = "ଓଡ଼ିଆ ପ୍ରତିଯୋଗିତାମୂଳକ ପରୀକ୍ଷା ପାଇଁ ଗୁରୁତ୍ୱପୂର୍ଣ୍ଣ ରୂଢ଼ି ଓ ଅର୍ଥ:",
                    formulasOrHighlights = listOf(
                        "ଅନ୍ଧାରରେ ବାଡ଼ି ବୁଲାଇବା - ଅନୁମାନ କରି କାମ କରିବା",
                        "ଆକାଶ କୁସୁମ - ଅସମ୍ଭବ କଥା",
                        "କପାଳ ଫିଟିବା - ଭଲ ସମୟ ଆସିବା",
                        "ଘରଢିଙ୍କି କୁମ୍ଭୀର - ନିଜ ଲୋକ ଶତ୍ରୁ ହେବା"
                    )
                )
            )
        ),
        SectionalSubject(
            id = "english",
            name = "English",
            pagesCount = 28,
            description = "Grammar rules, Vocabulary, Idioms, Error Spotting & Reading Comprehension",
            topics = listOf("Subject-Verb Agreement", "Tenses & Active/Passive", "Prepositions", "Idioms & Phrases"),
            studyNotes = listOf(
                StudyNote(
                    id = "eng_n1",
                    title = "Golden Rules of Subject-Verb Agreement",
                    content = "Essential rules tested regularly in SSC, Banking, and State PSC exams.",
                    formulasOrHighlights = listOf(
                        "Two singular subjects joined by 'and' require a plural verb (e.g. Gold and silver are precious).",
                        "Subjects joined by 'neither...nor', 'either...or' agree with the nearer subject.",
                        "Collective nouns take singular verbs when thought of as a single whole."
                    )
                )
            )
        ),
        SectionalSubject(
            id = "reasoning",
            name = "Reasoning",
            pagesCount = 22,
            description = "Verbal & Non-Verbal logic, Syllogism, Blood Relations, Coding-Decoding",
            topics = listOf("Coding-Decoding", "Blood Relations", "Direction Sense", "Syllogism"),
            studyNotes = listOf(
                StudyNote(
                    id = "reas_n1",
                    title = "Alphabet Positional Numbers (EJOTY)",
                    content = "Quick mental trick to recall 1-26 positions in letters instantly:",
                    formulasOrHighlights = listOf(
                        "E = 5, J = 10, O = 15, T = 20, Y = 25",
                        "Opposite pairs: A-Z, B-Y, C-X, D-W, E-V, F-U, G-T, H-S, I-R, J-Q, K-P, L-O, M-N",
                        "Sum of opposite letter positions is always 27."
                    )
                )
            )
        ),
        SectionalSubject(
            id = "history",
            name = "History",
            pagesCount = 18,
            description = "Ancient, Medieval, and Modern Indian Freedom Struggle timeline",
            topics = listOf("Indus Valley Civilization", "Mughal Empire", "1857 Revolt", "Gandhian Era 1915-1947"),
            studyNotes = listOf(
                StudyNote(
                    id = "hist_n1",
                    title = "Chronology of Freedom Movements",
                    content = "Major milestones in modern Indian freedom struggle:",
                    formulasOrHighlights = listOf(
                        "1905: Partition of Bengal & Swadeshi Movement",
                        "1919: Rowlatt Act & Jallianwala Bagh Massacre",
                        "1920: Non-Cooperation Movement launched",
                        "1930: Dandi March & Civil Disobedience Movement",
                        "1942: Quit India Resolution passed in Bombay"
                    )
                )
            )
        ),
        SectionalSubject(
            id = "geography",
            name = "Geography",
            pagesCount = 24,
            description = "Physical geography, Indian river systems, Climate, Soils, and Mineral resources",
            topics = listOf("Physiographic Divisions", "River Systems & Drainage", "Monsoon Mechanism", "Major Minerals"),
            studyNotes = listOf(
                StudyNote(
                    id = "geo_n1",
                    title = "Indian River Systems Comparison",
                    content = "Himalayan vs Peninsular River characteristics:",
                    formulasOrHighlights = listOf(
                        "Himalayan Rivers: Perennial, snow-fed, large deltas (Ganga, Indus, Brahmaputra).",
                        "Peninsular Rivers: Seasonal, rain-fed, flow in graded valleys (Godavari, Krishna, Mahanadi).",
                        "West-flowing rift valley rivers: Narmada and Tapi (form estuaries, not deltas)."
                    )
                )
            )
        ),
        SectionalSubject(
            id = "economics",
            name = "Economics",
            pagesCount = 16,
            description = "Macroeconomics, Indian Budget, Banking systems, Inflation, and NITI Aayog policies",
            topics = listOf("National Income (GDP, GNP)", "Monetary Policy & RBI", "Inflation Types", "Fiscal Policy"),
            studyNotes = listOf(
                StudyNote(
                    id = "eco_n1",
                    title = "RBI Monetary Policy Tools",
                    content = "Key quantitative instruments used by Reserve Bank of India:",
                    formulasOrHighlights = listOf(
                        "Repo Rate: Rate at which RBI lends money to commercial banks against government securities.",
                        "Reverse Repo: Rate at which RBI borrows funds from commercial banks.",
                        "CRR (Cash Reserve Ratio): Percentage of deposits banks must keep liquid with RBI.",
                        "SLR (Statutory Liquidity Ratio): Liquid asset reserve ratio maintained by banks."
                    )
                )
            )
        ),
        SectionalSubject(
            id = "polity",
            name = "Polity",
            pagesCount = 20,
            description = "Indian Constitution, Fundamental Rights, Parliament, Judiciary & Constitutional bodies",
            topics = listOf("Preamble & Fundamental Rights", "Directive Principles (DPSP)", "President & Parliament", "Supreme Court"),
            studyNotes = listOf(
                StudyNote(
                    id = "pol_n1",
                    title = "Fundamental Rights (Articles 12 - 35)",
                    content = "Six fundamental rights guaranteed under Part III:",
                    formulasOrHighlights = listOf(
                        "Right to Equality (Articles 14 - 18)",
                        "Right to Freedom (Articles 19 - 22)",
                        "Right against Exploitation (Articles 23 - 24)",
                        "Right to Freedom of Religion (Articles 25 - 28)",
                        "Cultural and Educational Rights (Articles 29 - 30)",
                        "Right to Constitutional Remedies (Article 32 - Heart and Soul)"
                    )
                )
            )
        ),
        SectionalSubject(
            id = "science",
            name = "Science",
            pagesCount = 26,
            description = "General Physics, Chemistry, Biology, Environmental science & Space tech",
            topics = listOf("Newton's Laws & Optics", "Periodic Table & Acids", "Human Anatomy & Diseases", "Ecology"),
            studyNotes = listOf(
                StudyNote(
                    id = "sci_n1",
                    title = "Important Vitamins & Deficiency Diseases",
                    content = "High-frequency questions in state and national prelims:",
                    formulasOrHighlights = listOf(
                        "Vitamin A (Retinol) -> Night Blindness, Xerophthalmia",
                        "Vitamin B1 (Thiamine) -> Beriberi",
                        "Vitamin C (Ascorbic Acid) -> Scurvy",
                        "Vitamin D (Calciferol) -> Rickets (children), Osteomalacia (adults)",
                        "Vitamin K (Phylloquinone) -> Delayed blood clotting"
                    )
                )
            )
        ),
        SectionalSubject(
            id = "computer",
            name = "Computer",
            pagesCount = 18,
            description = "Computer fundamentals, Networking, MS Office shortcuts, Cybersecurity, and DBMS",
            topics = listOf("Hardware & Architecture", "Operating Systems", "Networking & Internet", "Cyber Security & Keys"),
            studyNotes = listOf(
                StudyNote(
                    id = "comp_n1",
                    title = "OSI Model 7 Layers Breakdown",
                    content = "Remember top-down mnemonic: All People Seem To Need Data Processing",
                    formulasOrHighlights = listOf(
                        "7. Application Layer (HTTP, FTP, DNS)",
                        "6. Presentation Layer (Encryption, Compression)",
                        "5. Session Layer (Session setup & teardown)",
                        "4. Transport Layer (TCP, UDP, Ports)",
                        "3. Network Layer (IP, Routing, Routers)",
                        "2. Data Link Layer (Frames, MAC, Switches)",
                        "1. Physical Layer (Bits, Cables, Hubs)"
                    )
                )
            )
        ),
        SectionalSubject(
            id = "odisha_gk",
            name = "Odisha GK",
            pagesCount = 15,
            description = "Odisha history, geography, temples, culture, festivals, rivers, wildlife sanctuaries",
            topics = listOf("Kalinga War & Ashoka", "Ganga & Gajapati Dynasties", "Chilika & Similipal", "Odissi & Ratha Yatra"),
            studyNotes = listOf(
                StudyNote(
                    id = "ogk_n1",
                    title = "National Parks & Wildlife in Odisha",
                    content = "Vital conservation zones in Odisha:",
                    formulasOrHighlights = listOf(
                        "Similipal National Park: Located in Mayurbhanj, designated Biosphere Reserve and Tiger Reserve.",
                        "Bhitarkanika National Park: In Kendrapara, famous for Saltwater Crocodiles and Olive Ridley Turtles.",
                        "Chilika Lake: First Indian wetland of international importance under Ramsar Convention (1981).",
                        "Debrigarh Wildlife Sanctuary: Situated in Bargarh near Hirakud Dam."
                    )
                )
            )
        )
    )

    // Previous Year Papers matching Mockup #7
    val pyqPapers: List<PreviousYearPaper> = listOf(
        PreviousYearPaper(
            id = "pyq_upsc_2023",
            examName = "UPSC (Prelims)",
            year = 2023,
            pagesCount = 45,
            paperType = "General Studies Paper I",
            overview = "Complete authentic question paper with verified answer keys, analytical solutions, and subject-wise difficulty breakdown.",
            questionsSnapshot = listOf(
                "1. Consider the following statements regarding the G20 Common Framework...",
                "2. With reference to the Indian economy, demand-pull inflation can be caused by...",
                "3. In the context of India's biodiversity, consider the following species...",
                "4. Which one of the following explains the practice of 'Eco-friendly agriculture'?"
            )
        ),
        PreviousYearPaper(
            id = "pyq_opsc_2022",
            examName = "OPSC (Prelims)",
            year = 2022,
            pagesCount = 62,
            paperType = "General Studies Paper I & II",
            overview = "Odisha Public Service Commission Combined Competitive Prelims with state-specific Odisha GK, Indian Polity, and History solutions.",
            questionsSnapshot = listOf(
                "1. Which dynasty built the world-renowned Konark Sun Temple?",
                "2. The Hirakud Dam on the Mahanadi River was inaugurated in which year?",
                "3. What is the sex ratio of Odisha according to Census 2011?",
                "4. Identify the tribal rebellion led by Laxman Nayak during Quit India Movement."
            )
        ),
        PreviousYearPaper(
            id = "pyq_ssc_2021",
            examName = "SSC (CGL)",
            year = 2021,
            pagesCount = 48,
            paperType = "Tier 1 - Shift 1 to Shift 3",
            overview = "Staff Selection Commission Combined Graduate Level examination paper with Quantitative Aptitude and English solutions.",
            questionsSnapshot = listOf(
                "1. In a triangle ABC, if AD is median and AB = 10, AC = 14, BC = 12, find AD.",
                "2. Select the most appropriate synonym for the given word: 'DILIGENT'.",
                "3. Which Article of the Indian Constitution empowers High Courts to issue writs?",
                "4. Who among the following was the founder of the Sayyid Dynasty?"
            )
        ),
        PreviousYearPaper(
            id = "pyq_ossc_2020",
            examName = "OSSC (Junior Asst.)",
            year = 2020,
            pagesCount = 37,
            paperType = "Preliminary Examination",
            overview = "OSSC Junior Assistant preliminary exam covering Odia language, Computer knowledge, and Basic Math.",
            questionsSnapshot = listOf(
                "1. Choose the correctly spelled Odia word for 'ଆଶୀର୍ବାଦ'.",
                "2. Shortcut key to open Task Manager directly in Windows OS is...",
                "3. What is the LCM of 12, 18, and 24?",
                "4. In which year was Utkal Sammilani formed under the leadership of Madhusudan Das?"
            )
        ),
        PreviousYearPaper(
            id = "pyq_bank_2019",
            examName = "Bank PO",
            year = 2019,
            pagesCount = 52,
            paperType = "IBPS PO Prelims",
            overview = "Comprehensive banking prelims paper covering Data Interpretation, Seating Arrangement puzzles, and Reading Comprehension.",
            questionsSnapshot = listOf(
                "1. Read the passage and answer questions regarding Digital Payment growth.",
                "2. Eight persons P, Q, R, S, T, U, V, and W are sitting around a circular table facing center...",
                "3. Find the missing term in: 7, 14, 42, 168, 840, ?",
                "4. In what ratio must a grocer mix two varieties of tea worth ₹60/kg and ₹65/kg?"
            )
        ),
        PreviousYearPaper(
            id = "pyq_rrb_2021",
            examName = "Railway NTPC",
            year = 2021,
            pagesCount = 40,
            paperType = "CBT 1 Stage",
            overview = "Railway Recruitment Board Non-Technical Popular Categories Stage 1 question paper with general science answers.",
            questionsSnapshot = listOf(
                "1. What is the chemical formula of Washing Soda?",
                "2. Where is the headquarters of South Eastern Railway located?",
                "3. Which gland in the human body is called the 'Master Gland'?",
                "4. When was the First Passenger Train run in India between Bombay and Thane?"
            )
        )
    )

    // Admin Activity Log matching Mockup #8
    val adminActivityLogs: MutableList<AdminActivityLog> = mutableListOf(
        AdminActivityLog("act_1", "2026-06-18 10:24", "New practice question added (Science Set)", "admin"),
        AdminActivityLog("act_2", "2026-06-18 09:12", "PYQ uploaded (OPSC 2022 Paper I)", "admin"),
        AdminActivityLog("act_3", "2026-06-17 18:45", "Sectional PDF notes updated (Mathematics)", "admin"),
        AdminActivityLog("act_4", "2026-06-17 12:30", "Practice set created (Odisha GK Set 1)", "admin")
    )

    var adminMetrics: AdminMetric = AdminMetric(
        totalQuestions = 1250,
        practiceSets = 12,
        sectionalPdfs = 11,
        pyqFiles = 15
    )

    fun addNewQuestion(subject: String, question: String, options: List<String>, correctIndex: Int, explanation: String) {
        val newQ = Question(
            id = "q_${System.currentTimeMillis()}",
            subject = subject,
            questionText = question,
            options = options,
            correctIndex = correctIndex,
            explanation = explanation
        )
        // Add to first matching or create new set
        val targetSet = practiceSets.find { it.category.contains(subject, ignoreCase = true) || it.title.contains(subject, ignoreCase = true) }
        if (targetSet != null) {
            val updatedQuestions = targetSet.questions + newQ
            val idx = practiceSets.indexOf(targetSet)
            practiceSets[idx] = targetSet.copy(
                totalQuestions = updatedQuestions.size,
                questions = updatedQuestions
            )
        }
        adminMetrics = adminMetrics.copy(totalQuestions = adminMetrics.totalQuestions + 1)
        adminActivityLogs.add(0, AdminActivityLog(
            id = "act_${System.currentTimeMillis()}",
            date = "Just now",
            activity = "New question added to $subject",
            user = "admin"
        ))
    }
}
