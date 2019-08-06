/*
 * Copyright (C) 2019 - present Instructure, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.instructure.student.ui.renderTests

import android.os.Build
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.instructure.canvasapi2.models.Assignment
import com.instructure.canvasapi2.models.Course
import com.instructure.espresso.*
import com.instructure.panda_annotations.FeatureCategory
import com.instructure.panda_annotations.Priority
import com.instructure.panda_annotations.TestCategory
import com.instructure.panda_annotations.TestMetaData
import com.instructure.student.R
import com.instructure.student.espresso.StudentRenderTest
import com.instructure.student.mobius.assignmentDetails.submission.picker.PickerSubmissionMode
import com.instructure.student.mobius.assignmentDetails.submission.picker.ui.PickerListItemViewState
import com.instructure.student.mobius.assignmentDetails.submission.picker.ui.PickerSubmissionUploadFragment
import com.instructure.student.mobius.assignmentDetails.submission.picker.ui.PickerSubmissionUploadViewState
import com.instructure.student.mobius.assignmentDetails.submission.picker.ui.PickerVisibilities
import com.instructure.student.ui.pages.renderPages.PickerSubmissionUploadRenderPage
import com.spotify.mobius.runners.WorkRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PickerSubmissionUploadRenderTest : StudentRenderTest() {

    private val page = PickerSubmissionUploadRenderPage()
    private val baseVisibilities = PickerVisibilities(fab = true)

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysEmptyState() {
        loadPageWithViewState(PickerSubmissionUploadViewState.Empty(baseVisibilities))
        page.emptyView.assertVisible()
        page.fabPick.assertVisible()

        page.recycler.assertNotDisplayed()
        page.submitButton.check(doesNotExist())
        assertExtraFabsNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysEmptyStateWithLoading() {

        // API 23 doesn't do well with progress bars
        if(Build.VERSION.SDK_INT < 24) {
            return
        }

        loadPageWithViewState(PickerSubmissionUploadViewState.Empty(baseVisibilities.copy(loading = true)))
        page.emptyView.assertVisible()
        page.fabPick.assertVisible()
        page.loading.assertVisible()

        page.recycler.assertNotDisplayed()
        page.submitButton.check(doesNotExist())
        assertExtraFabsNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysListState() {
        val fileItemStates = listOf(
            PickerListItemViewState(0, R.drawable.vd_media_recordings, "title", "12.3 KB")
        )
        loadPageWithViewState(
            PickerSubmissionUploadViewState.FileList(
                baseVisibilities.copy(submit = true),
                fileItemStates
            )
        )
        page.recycler.assertVisible()
        page.fabPick.assertVisible()
        page.submitButton.assertVisible()

        page.emptyView.assertNotDisplayed()
        assertExtraFabsNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysListStateWithLoading() {

        // API 23 doesn't do well with progress bars
        if(Build.VERSION.SDK_INT < 24) {
            return
        }

        val fileItemStates = listOf(
            PickerListItemViewState(0, R.drawable.vd_media_recordings, "title", "12.3 KB")
        )
        loadPageWithViewState(
            PickerSubmissionUploadViewState.FileList(
                baseVisibilities.copy(submit = true, loading = true),
                fileItemStates
            )
        )
        page.recycler.assertVisible()
        page.fabPick.assertVisible()
        page.submitButton.assertVisible()
        page.loading.assertVisible()

        page.emptyView.assertNotDisplayed()
        assertExtraFabsNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysFabsAndHandlesClicks() {
        loadPageWithViewState(
            PickerSubmissionUploadViewState.Empty(
                baseVisibilities.copy(
                    fabCamera = true,
                    fabGallery = true,
                    fabFile = true
                )
            )
        )
        page.fabPick.assertVisible()
        assertExtraFabsNotDisplayed()

        // Perform click and assert displayed
        page.fabPick.click()
        assertExtraFabsDisplayed()

        // Test file click closes fab
        page.fabFile.click()
        assertExtraFabsNotDisplayed()

        // Perform click and assert displayed
        page.fabPick.click()
        assertExtraFabsDisplayed()

        // Test gallery click closes fab
        page.fabGallery.click()
        assertExtraFabsNotDisplayed()

        // Perform click and assert displayed
        page.fabPick.click()
        assertExtraFabsDisplayed()

        // Test camera click closes fab
        page.fabCamera.click()
        assertExtraFabsNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun showsOnlyFabCamera() {
        loadPageWithViewState(
            PickerSubmissionUploadViewState.Empty(
                baseVisibilities.copy(
                    fabCamera = true,
                    fabGallery = false,
                    fabFile = false
                )
            )
        )
        page.fabPick.assertVisible()
        assertExtraFabsNotDisplayed()

        // Perform click and assert displayed
        page.fabPick.click()
        page.fabCamera.assertDisplayed()

        page.fabFile.assertNotDisplayed()
        page.fabGallery.assertNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun showsOnlyFabGallery() {
        loadPageWithViewState(
            PickerSubmissionUploadViewState.Empty(
                baseVisibilities.copy(
                    fabCamera = false,
                    fabGallery = true,
                    fabFile = false
                )
            )
        )
        page.fabPick.assertVisible()
        assertExtraFabsNotDisplayed()

        // Perform click and assert displayed
        page.fabPick.click()
        page.fabGallery.assertDisplayed()

        page.fabFile.assertNotDisplayed()
        page.fabCamera.assertNotDisplayed()

    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun showsOnlyFabFile() {
        loadPageWithViewState(
            PickerSubmissionUploadViewState.Empty(
                baseVisibilities.copy(
                    fabCamera = false,
                    fabGallery = false,
                    fabFile = true
                )
            )
        )
        page.fabPick.assertVisible()
        assertExtraFabsNotDisplayed()

        // Perform click and assert displayed
        page.fabPick.click()
        page.fabFile.assertDisplayed()

        page.fabCamera.assertNotDisplayed()
        page.fabGallery.assertNotDisplayed()
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysCorrectStringsForSubmissionMode() {
        loadPageWithViewState(
            PickerSubmissionUploadViewState.Empty(baseVisibilities)
        )
        page.assertHasTitle(R.string.submission)
        page.emptyMessage.assertHasText(R.string.chooseFileSubtext)
    }

    @Test
    @TestMetaData(Priority.P3, FeatureCategory.SUBMISSIONS, TestCategory.RENDER)
    fun displaysCorrectStringsForCommentMode() {
        loadPageWithViewState(
            viewState = PickerSubmissionUploadViewState.Empty(baseVisibilities),
            mode = PickerSubmissionMode.CommentAttachment
        )
        page.assertHasTitle(R.string.commentUpload)
        page.emptyMessage.assertHasText(R.string.chooseFileForCommentSubtext)
    }

    private fun assertExtraFabsDisplayed() {
        page.fabFile.assertDisplayed()
        page.fabCamera.assertDisplayed()
        page.fabGallery.assertDisplayed()
    }

    private fun assertExtraFabsNotDisplayed() {
        page.fabFile.assertNotDisplayed()
        page.fabCamera.assertNotDisplayed()
        page.fabGallery.assertNotDisplayed()
    }

    private fun loadPageWithViewState(
        viewState: PickerSubmissionUploadViewState,
        mode: PickerSubmissionMode = PickerSubmissionMode.FileSubmission
    ): PickerSubmissionUploadFragment {
        val course = Course()
        val assignment = Assignment()

        val emptyEffectRunner = object : WorkRunner {
            override fun dispose() = Unit
            override fun post(runnable: Runnable) = Unit
        }
        val fragment = PickerSubmissionUploadFragment().apply {
            overrideInitViewState = viewState
            loopMod = { it.effectRunner { emptyEffectRunner } }
            arguments = PickerSubmissionUploadFragment.makeRoute(course, assignment, mode).arguments
        }
        activityRule.activity.loadFragment(fragment)
        return fragment
    }
}