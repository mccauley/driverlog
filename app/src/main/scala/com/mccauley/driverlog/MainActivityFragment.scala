package com.mccauley.driverlog

import android.app.Fragment
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}

class MainActivityFragment extends Fragment {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    inflater.inflate(R.layout.fragment_main, container, false)
  }
}