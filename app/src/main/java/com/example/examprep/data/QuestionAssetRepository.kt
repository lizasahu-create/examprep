package com.example.examprep.data

import android.content.res.AssetManager
import com.example.examprep.model.McqQuestion
import com.example.examprep.model.MockTestSet
import com.example.examprep.model.MockTestSetMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

import java.util.concurrent.ConcurrentHashMap

class QuestionAssetRepository(
    private val assetManager: AssetManager
) {
    private val cachedIndexByCategory = ConcurrentHashMap<String, List<MockTestSetMeta>>()

    suspend fun getSetsForCategory(categoryId: String): List<MockTestSetMeta> =
        withContext(Dispatchers.IO) {
            val indexPath = CATEGORY_INDEX_PATHS[categoryId] ?: return@withContext emptyList()
            cachedIndexByCategory[categoryId]?.let { return@withContext it }
            val jsonText = assetManager.open(indexPath)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            val parsed = parseIndexJson(jsonText)
            cachedIndexByCategory[categoryId] = parsed
            parsed
        }

    suspend fun loadAllCategorySets(): Map<String, List<MockTestSetMeta>> =
        withContext(Dispatchers.IO) {
            val result = LinkedHashMap<String, List<MockTestSetMeta>>()
            for ((categoryId, indexPath) in CATEGORY_INDEX_PATHS) {
                val cached = cachedIndexByCategory[categoryId]
                if (cached != null) {
                    result[categoryId] = cached
                } else {
                    val parsed = runCatching {
                        val jsonText = assetManager.open(indexPath)
                            .bufferedReader(Charsets.UTF_8)
                            .use { it.readText() }
                        parseIndexJson(jsonText)
                    }.getOrDefault(emptyList())
                    if (parsed.isNotEmpty()) {
                        cachedIndexByCategory[categoryId] = parsed
                        result[categoryId] = parsed
                    }
                }
            }
            result
        }

    suspend fun loadMockTestSet(meta: MockTestSetMeta): MockTestSet =
        withContext(Dispatchers.IO) {
            val jsonText = assetManager.open(meta.assetPath)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            parseSetJson(meta, jsonText)
        }

    companion object {
        const val PRACTICE_FULL_LENGTH_CATEGORY_ID = "practice_full_length"
        const val PRACTICE_GK_CATEGORY_ID = "practice_gk"
        const val PRACTICE_MATH_REASONING_CATEGORY_ID = "practice_math_reasoning"
        const val PRACTICE_ODIA_ENGLISH_CATEGORY_ID = "practice_odia_english"
        const val PYQ_OPSC_CATEGORY_ID = "pyq_opsc"
        const val FULL_LENGTH_INDEX_PATH = "practice_full_length/index.json"
        const val GK_INDEX_PATH = "practice_gk/index.json"
        const val MATH_REASONING_INDEX_PATH = "practice_math_reasoning/index.json"
        const val ODIA_ENGLISH_INDEX_PATH = "practice_odia_english/index.json"
        const val PYQ_OPSC_INDEX_PATH = "pyq_opsc/index.json"

        val CATEGORY_INDEX_PATHS: Map<String, String> = mapOf(
            PRACTICE_FULL_LENGTH_CATEGORY_ID to FULL_LENGTH_INDEX_PATH,
            PRACTICE_GK_CATEGORY_ID to GK_INDEX_PATH,
            PRACTICE_MATH_REASONING_CATEGORY_ID to MATH_REASONING_INDEX_PATH,
            PRACTICE_ODIA_ENGLISH_CATEGORY_ID to ODIA_ENGLISH_INDEX_PATH,
            PYQ_OPSC_CATEGORY_ID to PYQ_OPSC_INDEX_PATH
        )

        fun parseIndexJson(jsonText: String): List<MockTestSetMeta> {
            val root = JSONObject(jsonText)
            val setsArray = root.getJSONArray("sets")
            val result = ArrayList<MockTestSetMeta>(setsArray.length())
            for (i in 0 until setsArray.length()) {
                val obj = setsArray.getJSONObject(i)
                result.add(
                    MockTestSetMeta(
                        id = obj.getString("id"),
                        categoryId = obj.getString("categoryId"),
                        title = obj.getString("title"),
                        subtitle = obj.optString("subtitle", ""),
                        durationMinutes = obj.optInt("durationMinutes", 60),
                        negativeMarking = obj.optDouble("negativeMarking", 0.5),
                        marksPerQuestion = obj.optDouble("marksPerQuestion", 2.0),
                        questionCount = obj.optInt("questionCount", 100),
                        assetPath = obj.getString("assetPath")
                    )
                )
            }
            return result
        }

        fun parseSetJson(meta: MockTestSetMeta, jsonText: String): MockTestSet {
            val root = JSONObject(jsonText)
            val questionsArray = root.getJSONArray("questions")
            val questions = ArrayList<McqQuestion>(questionsArray.length())
            for (i in 0 until questionsArray.length()) {
                val qObj = questionsArray.getJSONObject(i)
                val optionsArray = qObj.getJSONArray("options")
                val options = ArrayList<String>(optionsArray.length())
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }
                val rawCtx = if (qObj.isNull("contextText")) null else qObj.optString("contextText", "").takeIf { it.isNotBlank() }
                val rawImg = if (qObj.isNull("imageAssetPath")) null else qObj.optString("imageAssetPath", "").takeIf { it.isNotBlank() }
                questions.add(
                    McqQuestion(
                        number = qObj.getInt("number"),
                        section = qObj.optString("section", ""),
                        contextText = rawCtx,
                        imageAssetPath = rawImg,
                        questionText = qObj.getString("questionText"),
                        options = options,
                        correctIndex = qObj.getInt("correctIndex")
                    )
                )
            }
            return MockTestSet(meta = meta, questions = questions)
        }
    }
}
