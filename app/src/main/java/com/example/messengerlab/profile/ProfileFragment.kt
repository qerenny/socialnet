package com.example.messengerlab.profile

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.messengerlab.R
import com.example.messengerlab.databinding.FragmentProfileBinding
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    private var defaultSpinnerBackground: Drawable? = null

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
        defaultSpinnerBackground = binding.profileStatusSpinner.background

        setupToolbar()
        setupSpinner()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.profileToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    viewModel.setEditing(true)
                    true
                }
                R.id.action_save -> {
                    viewModel.saveProfile()
                    viewModel.setEditing(false)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.profileStatusSpinner.adapter = adapter
    }

    private fun setupListeners() {
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

        // Username Listener
        binding.profileUsernameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onUsernameChanged(s.toString())
            }
        })

        // Spinner Listener
        binding.profileStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Изменение статуса разрешено только в режиме редактирования
                if (viewModel.isEditing.value == true) {
                    viewModel.onStatusChanged(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Birthday Listener (DatePicker)
        binding.profileBirthdayValue.setOnClickListener {
            if (viewModel.isEditing.value == true) {
                showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentBirthday = viewModel.birthday.value ?: 0L
        if (currentBirthday > 0) {
            calendar.timeInMillis = currentBirthday
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                viewModel.onBirthdayChanged(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeViewModel() {
        // Observe Edit Mode to invalidate Menu and toggle fields
        viewModel.isEditing.observe(viewLifecycleOwner) { isEditing ->
            val menu = binding.profileToolbar.menu
            val editItem = menu.findItem(R.id.action_edit)
            val saveItem = menu.findItem(R.id.action_save)

            if (editItem != null && saveItem != null) {
                editItem.isVisible = !isEditing
                saveItem.isVisible = isEditing
            }

            binding.profileNameInput.isEnabled = isEditing
            binding.profileBioInput.isEnabled = isEditing
            binding.profileUsernameInput.isEnabled = isEditing

            // 1. Управляем isEnabled
            binding.profileStatusSpinner.isEnabled = isEditing

            // 2. Управляем фоном для скрытия/отображения стрелки
            if (isEditing) {
                // Режим редактирования: восстанавливаем стандартный фон (со стрелкой)
                binding.profileStatusSpinner.background = defaultSpinnerBackground
            } else {
                // Режим просмотра: устанавливаем прозрачный фон (скрываем стрелку)
                // Если вы используете AndroidX, ContextCompat предпочтительнее.
                binding.profileStatusSpinner.background = ContextCompat.getDrawable(requireContext(), android.R.color.transparent)
            }
        }

        viewModel.name.observe(viewLifecycleOwner) { name ->
            if (binding.profileNameInput.text.toString() != name) {
                binding.profileNameInput.setText(name)
            }
            updateInitials(name)
        }

        viewModel.bio.observe(viewLifecycleOwner) { bio ->
            if (binding.profileBioInput.text.toString() != bio) {
                binding.profileBioInput.setText(bio)
            }
        }

        viewModel.username.observe(viewLifecycleOwner) { username ->
            if (binding.profileUsernameInput.text.toString() != username) {
                binding.profileUsernameInput.setText(username)
            }
        }

        viewModel.birthday.observe(viewLifecycleOwner) { birthday ->
            binding.profileBirthdayValue.text = if (birthday > 0) {
                formatDate(birthday)
            } else {
                ""
            }
        }

        viewModel.statusIndex.observe(viewLifecycleOwner) { index ->
            if (binding.profileStatusSpinner.selectedItemPosition != index) {
                binding.profileStatusSpinner.setSelection(index)
            }
        }
    }

    private fun updateInitials(name: String) {
        val initials = name.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
            .uppercase()
        binding.profileAvatarInitials.text = initials
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return format.format(date)
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