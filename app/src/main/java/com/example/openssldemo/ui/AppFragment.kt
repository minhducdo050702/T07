package com.example.openssldemo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openssldemo.R
import com.example.openssldemo.adapter.AppAdapter
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.database.data.Log
import com.example.openssldemo.databinding.FragmentAppBinding
import com.example.openssldemo.viewmodel.AppViewModel


class AppFragment : Fragment() {


    private lateinit var binding: FragmentAppBinding

    private val viewModel : AppViewModel by activityViewModels {
        AppViewModel.AppViewModelFactory(
            requireActivity().application,
            AppDatabase.getDatabase(requireContext()).appDao(),
            AppDatabase.getDatabase(requireContext()).logDao()
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.appList.setHasFixedSize(true)
        binding.appList.layoutManager = LinearLayoutManager(this.context)
        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        binding.appList.addItemDecoration(divider)

        viewModel.appList.observe(viewLifecycleOwner) {appList ->
            val adapter = AppAdapter(appList)
            binding.appList.adapter = adapter
            android.util.Log.d("AppFragment", "onViewCreated: ${appList.size}")
        }

    }

    companion object {

    }
}