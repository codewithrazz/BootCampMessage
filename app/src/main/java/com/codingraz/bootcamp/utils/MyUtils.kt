package com.codingraz.bootcamp.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.codingraz.bootcamp.R

fun fragmentAdd(activity: FragmentActivity?, fragment: Fragment, bundle: Bundle? = null) {


    fragment.arguments = bundle

    val sn = "${fragment.javaClass.simpleName}"

    activity?.supportFragmentManager?.beginTransaction()?.apply {
        addToBackStack(sn)
        add(R.id.containerView, fragment, sn)
        commit()

    }

}