/*
 * Copyright (C) 2021 - present Instructure, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.instructure.pandautils.features.dashboard.notifications

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.instructure.pandautils.R
import com.instructure.pandautils.databinding.FragmentDashboardNotificationsBinding
import com.instructure.pandautils.discussions.DiscussionUtils
import com.instructure.pandautils.features.elementary.homeroom.HomeroomAction
import com.instructure.pandautils.utils.ColorKeeper
import com.instructure.pandautils.utils.asChooserExcludingInstructure
import com.instructure.pandautils.utils.bind
import com.instructure.pandautils.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardNotificationsFragment : Fragment() {

    @Inject
    lateinit var dashboardRouter: DashboardRouter

    private val viewModel: DashboardNotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDashboardNotificationsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.loadData(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.events.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                handleAction(it)
            }
        })
    }

    fun refresh() {
        viewModel.loadData(true)
    }

    private fun handleAction(action: DashboardNotificationsActions) {
        when (action) {
            is DashboardNotificationsActions.LaunchConference -> {
                val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ColorKeeper.getOrGenerateColor(action.canvasContext))
                        .build()

                var intent = CustomTabsIntent.Builder()
                        .setDefaultColorSchemeParams(colorSchemeParams)
                        .setShowTitle(true)
                        .build()
                        .intent

                intent.data = Uri.parse(action.url)

                intent = intent.asChooserExcludingInstructure()
                requireContext().startActivity(intent)
            }
            is DashboardNotificationsActions.ShowToast -> Toast.makeText(
                requireContext(),
                action.toast,
                Toast.LENGTH_SHORT
            ).show()
            is DashboardNotificationsActions.OpenAnnouncement -> dashboardRouter.routeToGlobalAnnouncement(
                action.subject,
                action.message
            )
        }
    }

    companion object {
        fun newInstance() = DashboardNotificationsFragment()
    }
}