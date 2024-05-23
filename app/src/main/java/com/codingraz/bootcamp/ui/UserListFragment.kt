package com.codingraz.bootcamp.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingraz.bootcamp.adapter.UserAdapter
import com.codingraz.bootcamp.databinding.FragmentUserListBinding
import com.codingraz.bootcamp.model.UserModel
import com.codingraz.bootcamp.utils.fragmentAdd
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserListFragment : Fragment() {

    private lateinit var binding: FragmentUserListBinding

    private val users = arrayListOf<UserModel>()

    private var userAdapter: UserAdapter? = null

    private val db = Firebase.firestore

    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            userModel = it.getParcelable("USER_MODEL") as? UserModel?
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = UserAdapter(users)

        binding.rvMain.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvMain.adapter = userAdapter

        userAdapter?.setItemClick {
            val bundle = Bundle().apply {
                putParcelable("USER_ANOTHER_MODEL", it)
                putParcelable("USER_MODEL", userModel)
            }

            fragmentAdd(activity, ConversationFragment(), bundle)
        }

        loadData()

        setUserToFirestore()


    }

    private fun setUserToFirestore() {
        val user = UserModel(
            userModel?.userId ?: "",
            userModel?.userName ?: "",
            userModel?.userImage ?: "",
            ""
        )

// Add a new document with a generated ID
        db.collection("user")
            .document(userModel?.userId ?: "")
            .set(user)
            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
            }

    }

    private fun loadData() {


        db.collection("user")
            .addSnapshotListener { snapshots, e ->

                snapshots?.documents?.let { docs ->


                    users.clear()
                    for (dc in docs) {

                        dc.data?.let { d ->


                            val uId = d.get("userId") as? String?
                            val uName = d.get("userName") as? String?
                            val uImage = d.get("userImage") as? String?
                            val lm = d.get("lastMessage") as? String?

                            val myUid = userModel?.userId ?: ""

                            if (uId != myUid) {
                                users.add(
                                    UserModel(
                                        uId ?: "",
                                        uName ?: "",
                                        uImage ?: "",
                                        lm ?: ""
                                    )
                                )
                            }


                        }
                    }

                    activity?.runOnUiThread {
                        userAdapter?.notifyDataSetChanged()
                    }

                }

            }

    } // loadData End Here==========

}