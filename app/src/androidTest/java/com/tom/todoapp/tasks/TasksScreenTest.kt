package com.tom.todoapp.tasks

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.tom.todoapp.HiltTestActivity
import com.tom.todoapp.R
import com.tom.todoapp.data.TaskRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class TasksScreenTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: TaskRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    /*    @Test
        fun displayTask_whenRepositoryHasData() = runTest {
            // GIVEN - One task already in the repository
            repository.createTask("TITLE1", "DESCRIPTION1")

            // WHEN - On startup
            setContent()

            // THEN - Verify task is displayed on screen
            composeTestRule.onNodeWithText("TITLE1").assertIsDisplayed()
        }*/

    private fun setContent() {
        composeTestRule.setContent {
            Surface {
                TaskScreen(
                    taskViewModel = TaskViewModel(repository, SavedStateHandle()),
                    userMsg = R.string.successfully_added_task_message,
                    onAddTask = { },
                    onTaskClick = { },
                    openDrawer = { }
                )
            }
        }
    }
}