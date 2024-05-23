package com.codingraz.bootcamp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingraz.bootcamp.databinding.FragmentHomeBinding
import com.codingraz.bootcamp.model.UserModel
import com.codingraz.bootcamp.utils.fragmentAdd


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.btnEnter.setOnClickListener {

            val uId = binding.etUserId.text?.toString()
            val uName = binding.etUserName.text?.toString()

            val userModel = UserModel(
                uId ?: "",
                uName ?: "",
                "",
                ""
            )

            val bundle = Bundle().apply {
                putParcelable("USER_MODEL", userModel)
            }

            fragmentAdd(activity, UserListFragment(), bundle)
        }

    }

}