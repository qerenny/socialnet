package com.example.messengerlab.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.messengerlab.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private val statusOptions = listOf(
        "Online",
        "Sleeping",
        "Working",
        "Doing my best",
        "Offline"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        setupSpinner()
        setupListeners()
        observeViewModel()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.profileStatusSpinner.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            viewModel.setEditing(true)
        }

        binding.btnSaveProfile.setOnClickListener {
            viewModel.saveProfile()
            viewModel.setEditing(false)
        }

        // Two-way binding: Update ViewModel on input change
        binding.profileNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onNameChanged(s.toString())
            }
        })

        binding.profileBioInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onBioChanged(s.toString())
            }
        })

        binding.profileStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.onStatusChanged(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeViewModel() {
        viewModel.name.observe(viewLifecycleOwner) { name ->
            if (binding.profileNameInput.text.toString() != name) {
                binding.profileNameInput.setText(name)
            }
        }

        viewModel.bio.observe(viewLifecycleOwner) { bio ->
            if (binding.profileBioInput.text.toString() != bio) {
                binding.profileBioInput.setText(bio)
            }
        }

        viewModel.statusIndex.observe(viewLifecycleOwner) { index ->
            if (binding.profileStatusSpinner.selectedItemPosition != index) {
                binding.profileStatusSpinner.setSelection(index)
            }
        }

        viewModel.isEditing.observe(viewLifecycleOwner) { isEditing ->
            binding.profileNameInput.isEnabled = isEditing
            binding.profileBioInput.isEnabled = isEditing
            binding.profileStatusSpinner.isEnabled = isEditing

            binding.btnEditProfile.visibility = if (isEditing) View.GONE else View.VISIBLE
            binding.btnSaveProfile.visibility = if (isEditing) View.VISIBLE else View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}
