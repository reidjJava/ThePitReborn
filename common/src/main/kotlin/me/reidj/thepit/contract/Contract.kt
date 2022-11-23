package me.reidj.thepit.contract

data class Contract(
    val title: String,
    val description: String,
    val type: ContractType,
    val condition: Int,
    var now: Int = 0,
    val money: Double
)
