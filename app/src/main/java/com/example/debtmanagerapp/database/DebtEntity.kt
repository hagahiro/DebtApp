package com.example.debtmanagerapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.ls.LSSerializer
import java.io.Serializable


@Entity
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creditor: String, // 借入先
    val loanDate: String, // 借入日（文字列形式）
    val amount: Int, // 借入金額（整数）
    val interestRate: Double, // 借入利率
    val repaymentMethod: String, // 返済方法
    val monthlyRepaymentAmount: Int?, // 毎月の返済額（整数、null許容）
    val numberOfInstallments: Int?, // 分割回数（null許容）
    val monthlyRepaymentDate: Int, // 毎月の返済日（月の日付、null不許容）
    val note: String = "" // メモ（空白許容、null不許容、デフォルト値は空文字）
): Serializable