package com.example.debtmanagerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.debtmanagerapp.database.DebtEntity
import com.example.debtmanagerapp.database.DebtViewModel
import com.example.debtmanagerapp.databinding.FragmentDashboardBinding
import com.example.debtmanagerapp.databinding.FragmentInputBinding
import com.example.debtmanagerapp.recyclerview.DebtAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DebtViewModel
    private lateinit var debtAdapter: DebtAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DebtViewModel::class.java)


        // LiveData の監視
        viewModel.getAllDebtsLiveData().observe(viewLifecycleOwner, Observer { debts ->
            // データが変更されたとき、RecyclerViewのアダプターを更新
            debtAdapter.updateData(debts)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debtAdapter = DebtAdapter(emptyList()) { debt ->
            navigateToInputFragment(debt)
        }
        binding.recyclerView.adapter = debtAdapter
    }

    private fun navigateToInputFragment(debt: DebtEntity) {
        val inputFragment = InputFragment().apply {
            arguments = Bundle().apply {
                putSerializable("debt_entity", debt)
            }
        }

        // フラグメントのトランザクションを実行
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, inputFragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
