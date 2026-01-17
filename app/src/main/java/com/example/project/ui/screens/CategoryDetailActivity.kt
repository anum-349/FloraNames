package com.example.project.ui.screens

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.dao.CategoryDao
import com.example.project.dao.NameDao
import com.example.project.models.Gender
import com.example.project.models.Name
import com.example.project.ui.adapters.CategoryAdapter

class CategoryDetailActivity : BaseActivity() {

    private val pageSize = 15
    private var currentOffset = 0
    private var isLoading = false
    private var allLoaded = false

    private lateinit var mydb: CategoryDao
    private lateinit var namedb: NameDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var alphabetIndex: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var btnFilter: ImageButton
    private lateinit var categoryName: TextView
    private lateinit var adapter: CategoryAdapter

    private var category = ""
    private var name1 = ""
    private var name2 = ""
    private var activeLetter: Char? = null
    private val letterViewMap = mutableMapOf<Char, TextView>()

    private var categoryId = -1
    private val allNames = mutableListOf<Name>()
    private val filteredNames = mutableListOf<Name>()
    private val letterOffsetMap = mutableMapOf<Char, Int>()

    private val filtersMap = mutableMapOf(
        "Boy" to false,
        "Girl" to false,
        "Short" to false,
        "Long" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        categoryId = intent.getIntExtra("category", -1)
        category = intent.getStringExtra("categoryName") ?: "Category"
        name1 = intent.getStringExtra("name1") ?: ""
        name2 = intent.getStringExtra("name2") ?: ""

        bindViews()
        mydb = CategoryDao(this)
        namedb = NameDao(this)
        categoryName.text = category

        adapter = CategoryAdapter { id ->
            val intent = Intent(this, NameDetailsActivity::class.java)
            intent.putExtra("name_id", id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && !allLoaded && lastVisibleItem >= totalItemCount - 2) {
                    loadNextPage(etSearch.text.toString(), reset = false)
                }
            }
        })

        loadAllNames()
        setupSearch()
        setupFilterButton()
        buildAlphabetIndex()
        setupFastScroll()
    }

    private fun bindViews() {
        recyclerView = findViewById(R.id.recyclerNames)
        alphabetIndex = findViewById(R.id.alphabetIndex)
        etSearch = findViewById(R.id.etSearch)
        btnFilter = findViewById(R.id.btnFilter)
        categoryName = findViewById(R.id.category_name)
    }

    private fun loadAllNames() {
        allNames.clear()
        if(category == "Category"){
            allNames.addAll(mydb.getNamesWithCategory(categoryId).map { it.name })
        } else if(category == "Name Twining"){
            allNames.addAll(namedb.getSiblingNames(name1).map { it.name })
        } else if(category == "Parent Names"){
            allNames.addAll(namedb.getMatchingNames(name1, name2).map { it.name })
        }
        applyFiltersAndSearch()
    }

    private fun applyFiltersAndSearch(searchQuery: String? = null) {
        val search = searchQuery?.lowercase() ?: ""

        filteredNames.clear()
        filteredNames.addAll(
            allNames.filter { name ->
                // Search match
                name.name.lowercase().contains(search) &&
                        // Filters match
                        filtersMap.all { (filter, enabled) ->
                            !enabled || nameMatchesFilter(name, filter)
                        }
            }
        )

        currentOffset = filteredNames.size.coerceAtMost(pageSize)
        allLoaded = filteredNames.size <= pageSize
        adapter.submitList(filteredNames.take(currentOffset).toList())
        buildAlphabetIndex()
    }

    private fun loadNextPage(searchQuery: String? = null, reset: Boolean = false) {
        if (isLoading || allLoaded) return
        isLoading = true

        if (reset) {
            currentOffset = 0
            allLoaded = false
        }

        val remaining = filteredNames.drop(currentOffset)
        val nextPage = remaining.take(pageSize)
        adapter.submitList(filteredNames.take(currentOffset) + nextPage)
        currentOffset += nextPage.size

        if (currentOffset >= filteredNames.size) allLoaded = true
        isLoading = false
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFiltersAndSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilterButton() {
        btnFilter.setOnClickListener {
            val keys = filtersMap.keys.toTypedArray()
            val checked = filtersMap.values.toBooleanArray()

            AlertDialog.Builder(this)
                .setTitle("Apply Filters")
                .setMultiChoiceItems(keys, checked) { _, which, isChecked ->
                    filtersMap[keys[which]] = isChecked
                }
                .setPositiveButton("Apply") { dialog, _ ->
                    applyFiltersAndSearch(etSearch.text.toString())
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun nameMatchesFilter(name: Name, filter: String) = when (filter) {
        "Boy" -> name.gender == Gender.BOY
        "Girl" -> name.gender == Gender.GIRL
        "Short" -> name.name.length <= 4
        "Long" -> name.name.length >= 5
        else -> true
    }

    private fun buildAlphabetIndex() {
        alphabetIndex.removeAllViews()
        letterOffsetMap.clear()
        letterViewMap.clear()

        filteredNames.forEachIndexed { index, name ->
            val letter = name.name.first().uppercaseChar()
            if (!letterOffsetMap.containsKey(letter)) {
                letterOffsetMap[letter] = index
            }
        }

        letterOffsetMap.keys.sorted().forEach { letter ->
            val view = createLetterView(letter)
            letterViewMap[letter] = view
            alphabetIndex.addView(view)
        }
    }

    private fun setActiveLetter(letter: Char) {
        if (activeLetter == letter) return

        // Reset previous
        activeLetter?.let {
            letterViewMap[it]?.background =
                getDrawable(R.drawable.bg_letter_inactive)
        }

        // Set new
        letterViewMap[letter]?.background =
            getDrawable(R.drawable.bg_letter_active)

        activeLetter = letter
    }

    private fun createLetterView(letter: Char): TextView {
        return TextView(this).apply {
            text = letter.toString()
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(6, 10, 6, 10)

            setTextColor(resources.getColor(R.color.colorPrimary, theme))
            background = getDrawable(R.drawable.bg_letter_inactive)

            setOnClickListener {
                setActiveLetter(letter)
                scrollToLetter(letter)
            }
        }
    }

    private fun setupFastScroll() {
        alphabetIndex.setOnTouchListener { _, event ->
            val height = alphabetIndex.height
            if (height == 0 || letterOffsetMap.isEmpty()) return@setOnTouchListener true

            val letters = letterOffsetMap.keys.sorted()
            val index = ((event.y / height) * letters.size)
                .toInt()
                .coerceIn(0, letters.size - 1)

            val letter = letters[index]
            setActiveLetter(letter)
            scrollToLetter(letter)

            true
        }
    }

    private fun scrollToLetter(letter: Char) {
        val position = letterOffsetMap[letter] ?: return
        (recyclerView.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(position, 0)
    }
}
