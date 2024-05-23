package com.example.openssldemo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openssldemo.R
import com.example.openssldemo.adapter.LogAdapter
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.databinding.FragmentLogBinding
import com.example.openssldemo.viewmodel.AppViewModel


class LogFragment : Fragment() {
    private lateinit var binding: FragmentLogBinding

    private val viewModel: AppViewModel by activityViewModels {
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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.logList.layoutManager = LinearLayoutManager(this.context)
        binding.logList.setHasFixedSize(true)
        val dividerItemDecoration = DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL)
        binding.logList.addItemDecoration(dividerItemDecoration)
        viewModel.logList.observe(viewLifecycleOwner) { logsList ->
            logsList?.let {
                val adapter = LogAdapter(logsList, requireContext())
                binding.logList.adapter = adapter

            }

        }
        binding.goToAppList.setOnClickListener {
            handleClick()
        }

    }
    private fun handleClick() {
        val action = LogFragmentDirections.actionLogFragmentToAppFragment()
        findNavController().navigate(action)
    }
}