package com.example.debtmanagerapp

//import android.R
import com.example.debtmanagerapp.R
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.debtmanagerapp.database.DebtEntity
import com.example.debtmanagerapp.database.DebtViewModel
import com.example.debtmanagerapp.databinding.FragmentInputBinding
import java.util.Calendar

class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DebtViewModel
    private var existingDebt: DebtEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DebtViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        existingDebt = arguments?.getSerializable("debt_entity") as? DebtEntity
        val repaymentMethods = listOf("リボ払い", "分割払い")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            repaymentMethods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.repaymentMethodSpinner.adapter = adapter

        val repaymentDates = (1..31).map { it.toString() }
        val repaymentDateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            repaymentDates
        )
        repaymentDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthlyRepaymentDateSpinner.adapter = repaymentDateAdapter

        val debtEntity = arguments?.getSerializable("debt_entity") as? DebtEntity

        setupUI()
        if (debtEntity != null) {
            // 編集モードの場合、編集ボタンを表示
            binding.editButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.VISIBLE
            binding.saveButton.visibility = View.GONE

            // DebtEntity のデータをUIに設定
        } else {
            // 新規作成モードの場合、編集ボタンを非表示
            binding.editButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE

        }

        debtEntity?.let {
            // 借入先
            binding.creditorEditText.setText(it.creditor)
            // 借入日
            binding.loanDateEditText.setText(it.loanDate)
            // 借入金額
            binding.amountEditText.setText(it.amount.toString())
            // 借入利率
            binding.interestRateEditText.setText(it.interestRate.toString())
            // 返済方法

            val repaymentMethodsIndex = repaymentMethods.indexOf(it.repaymentMethod)
            if (repaymentMethodsIndex != -1) {
                binding.repaymentMethodSpinner.setSelection(repaymentMethodsIndex)
            }
            // この部分はSpinnerまたは他の選択コンポーネントに依存するため、
            // 選択肢と一致させるロジックが必要です。
//            binding.repaymentMethodSpinner.setSelection(findRepaymentMethodIndex(it.repaymentMethod))
            // 毎月の返済額
            it.monthlyRepaymentAmount?.let { amount ->
                binding.monthlyRepaymentAmountEditText.setText(amount.toString())
            }
            // 分割回数
            it.numberOfInstallments?.let { installments ->
                binding.numberOfInstallmentsEditText.setText(installments.toString())
            }
            // 毎月の返済日
            val repaymentDateIndex = repaymentDates.indexOf(it.monthlyRepaymentDate.toString())
            if (repaymentDateIndex != -1) {
                binding.monthlyRepaymentDateSpinner.setSelection(repaymentDateIndex)
            }// メモ
            binding.noteEditText.setText(it.note)
        }

        binding.deleteButton.setOnClickListener {
            val debtEntity = arguments?.getSerializable("debt_entity") as? DebtEntity
            debtEntity?.let {
                viewModel.deleteDebt(it)
                // 削除後に必要な処理（例: フラグメントを閉じる、トーストを表示するなど）

            }

        }

        binding.editButton.setOnClickListener {
            val debtEntity = createDebtEntityFromInputs(existingDebt)
            if (debtEntity != null) {
                viewModel.updateDebt(debtEntity)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DashboardFragment())
                    .addToBackStack(null)
                    .commit()            } else {
                // 入力エラーがある場合の処理
            }
        }
    }

    private fun setupUI() {

        // 日付ピッカーダイアログのセットアップ
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                binding.loanDateEditText.setText(
                    String.format(
                        "%d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth
                    )
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        binding.loanDateEditText.setOnClickListener {
            datePickerDialog.show()
        }

        val daysOfMonth = (1..31).map { "${it}日" }.toTypedArray() // 「1日」、「2日」...という形式
        binding.monthlyRepaymentDateSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            daysOfMonth
        )

        // 返済方法スピナーのセットアップ
        val repaymentMethods = arrayOf("リボ払い", "分割払い")
        binding.repaymentMethodSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, repaymentMethods)
        binding.repaymentMethodSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> { // リボ払い
                            binding.monthlyRepaymentAmountEditText.visibility = View.VISIBLE
                            binding.numberOfInstallmentsEditText.visibility = View.GONE
                        }

                        1 -> { // 分割払い
                            binding.monthlyRepaymentAmountEditText.visibility = View.GONE
                            binding.numberOfInstallmentsEditText.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // 何も選択されていない場合の処理（通常は必要ありません）
                }
            }

        // 保存ボタンのクリックリスナー
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveDebtData()

            } else {
                Toast.makeText(context, "必須項目を入力してください", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveDebtData() {
        val creditor = binding.creditorEditText.text.toString()
        val loanDate = binding.loanDateEditText.text.toString()
        val amount = binding.amountEditText.text.toString().toIntOrNull() ?: 0
        val interestRate = binding.interestRateEditText.text.toString().toDoubleOrNull() ?: 0.0
        val repaymentMethod = binding.repaymentMethodSpinner.selectedItem.toString()
        val monthlyRepaymentAmount =
            binding.monthlyRepaymentAmountEditText.text.toString().toIntOrNull()
        val numberOfInstallments =
            binding.numberOfInstallmentsEditText.text.toString().toIntOrNull()
        val monthlyRepaymentDateStr = binding.monthlyRepaymentDateSpinner.selectedItem.toString()
        val monthlyRepaymentDateInt = parseDayOfMonth(monthlyRepaymentDateStr)

        val note = binding.noteEditText.text.toString()

        // データの妥当性を確認することもお勧めします

        if (monthlyRepaymentDateInt != null) {
            // 正常な数値に変換できた場合の処理
            val debt = DebtEntity(
                creditor = creditor,
                loanDate = loanDate,
                amount = amount,
                interestRate = interestRate,
                repaymentMethod = repaymentMethod,
                monthlyRepaymentAmount = monthlyRepaymentAmount,
                numberOfInstallments = numberOfInstallments,
                monthlyRepaymentDate = monthlyRepaymentDateInt,
                note = note
            )

            // ViewModelを介してデータベースに保存
            viewModel.insertDebt(debt)
        } else {
            // 数値に変換できなかった場合のエラーハンドリング
            // 例: ユーザーにエラーメッセージを表示するなど
        }
        // DebtEntity インスタンスを作成

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateInputs(): Boolean {
        // 必須項目が入力されているかチェック
        val isCreditorFilled = binding.creditorEditText.text.isNotBlank()
        val isLoanDateFilled = binding.loanDateEditText.text.isNotBlank()
        val isAmountFilled = binding.amountEditText.text.isNotBlank()
        val isInterestRate = binding.interestRateEditText.text.isNotBlank()
        val repaymentMethod = binding.repaymentMethodSpinner.selectedItem.toString()
        return when (repaymentMethod) {
            "リボ払い" -> {
                val isMonthlyRepaymentAmountFilled =
                    binding.monthlyRepaymentAmountEditText.text.isNotBlank()
                isCreditorFilled && isLoanDateFilled && isAmountFilled && isMonthlyRepaymentAmountFilled
            }

            "分割払い" -> {
                val isNumberOfInstallmentsFilled =
                    binding.numberOfInstallmentsEditText.text.isNotBlank()
                isCreditorFilled && isLoanDateFilled && isAmountFilled && isNumberOfInstallmentsFilled
            }

            else -> false
        }

        return isCreditorFilled && isLoanDateFilled && isAmountFilled && isInterestRate
    }

    private fun parseDayOfMonth(dateText: String): Int? {
        val numericPart = dateText.replace("[^0-9]".toRegex(), "") // 数値部分の抽出
        return numericPart.toIntOrNull()
    }

    private fun createDebtEntityFromInputs(existingDebt: DebtEntity?): DebtEntity? {
        val creditor = binding.creditorEditText.text.toString()
        val loanDate = binding.loanDateEditText.text.toString()
        val amount = binding.amountEditText.text.toString().toIntOrNull()
        val interestRate = binding.interestRateEditText.text.toString().toDoubleOrNull()
        val repaymentMethod = binding.repaymentMethodSpinner.selectedItem.toString()
        val monthlyRepaymentAmount =
            binding.monthlyRepaymentAmountEditText.text.toString().toIntOrNull()
        val numberOfInstallments =
            binding.numberOfInstallmentsEditText.text.toString().toIntOrNull()
        val monthlyRepaymentDate =
            binding.monthlyRepaymentDateSpinner.selectedItem.toString().toIntOrNull()
        val note = binding.noteEditText.text.toString()

        // 検証
        if (creditor.isBlank() || loanDate.isBlank() || amount == null || interestRate == null) {
            // 必須項目が未入力または不正な場合
            return null
        }
        when (repaymentMethod) {
            "リボ払い" -> if (monthlyRepaymentAmount == null) return null
            "分割払い" -> if (numberOfInstallments == null) return null
        }
        return DebtEntity(
            id = existingDebt?.id ?: 0, // 既存のID、もしくは新規の場合は0
            creditor = creditor,
            loanDate = loanDate,
            amount = amount,
            interestRate = interestRate,
            repaymentMethod = repaymentMethod,
            monthlyRepaymentAmount = monthlyRepaymentAmount,
            numberOfInstallments = numberOfInstallments,
            monthlyRepaymentDate = monthlyRepaymentDate ?: 0, // nullの場合はデフォルト値を設定
            note = note
        )
    }
}