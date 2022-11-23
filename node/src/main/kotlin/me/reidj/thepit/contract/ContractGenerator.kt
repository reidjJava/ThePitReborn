package me.reidj.thepit.contract

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object ContractGenerator {

    private fun create(
        title: String,
        lore: String,
        condition: Int,
        type: ContractType,
        money: Double
    ): Contract = Contract(title, lore, type, condition, 0, money)

    private val common = HashSet(
        listOf(
            create("Контракт1", "Убить 1 игрока", 1, ContractType.KILL, 20.0),
            create("Контракт2", "Убить 2 игроков", 2, ContractType.KILL, 20.0),
            create("Контракт3", "Убить 3 игроков", 3, ContractType.KILL, 20.0),
            create("Контракт4", "Убить 4 игроков", 4, ContractType.KILL, 20.0),
            create("Контракт5", "Квест на поинты66", 5, ContractType.POINTS, 20.0),
            create("Контракт6", "Квест на поинты77", 6, ContractType.POINTS, 20.0),
            create("Контракт7", "Квест на поинты88", 7, ContractType.POINTS, 20.0),
            create("Контракт8", "Квест на поинты99", 8, ContractType.POINTS, 20.0)
        )
    )

    private val rare = HashSet(
        listOf(
            create("Контракт9", "Убить 9 игроков", 9, ContractType.KILL, 20.0),
            create("Контракт10", "Убить 10 игроков", 10, ContractType.KILL, 20.0),
            create("Контракт11", "Убить 11 игроков", 11, ContractType.KILL, 20.0),
            create("Контракт12", "Убить 12 игроков", 12, ContractType.KILL, 20.0),
            create("Контракт13", "Квест на поинты44", 13, ContractType.POINTS, 20.0),
            create("Контракт14", "Квест на поинты11", 14, ContractType.POINTS, 20.0),
            create("Контракт15", "Квест на поинты22", 15, ContractType.POINTS, 20.0),
            create("Контракт16", "Квест на поинты33", 16, ContractType.POINTS, 20.0)
        )
    )

    private val epic = HashSet(
        listOf(
            create("Контракт17", "Убить 17 игроков", 17, ContractType.KILL, 20.0),
            create("Контракт18", "Убить 18 игроков", 18, ContractType.KILL, 20.0),
            create("Контракт19", "Убить 19 игроков", 19, ContractType.KILL, 20.0),
            create("Контракт20", "Убить 20 игроков", 20, ContractType.KILL, 20.0),
            create("Контракт21", "Квест на поинты9", 21, ContractType.POINTS, 20.0),
            create("Контракт22", "Квест на поинты8", 22, ContractType.POINTS, 20.0),
            create("Контракт23", "Квест на поинты76", 23, ContractType.POINTS, 20.0),
            create("Контракт24", "Квест на поинты6", 24, ContractType.POINTS, 20.0)
        )
    )

    private val legendary = HashSet(
        listOf(
            create("Контракт17", "Убить 17 игроков", 17, ContractType.KILL, 20.0),
            create("Контракт18", "Убить 18 игроков", 18, ContractType.KILL, 20.0),
            create("Контракт19", "Убить 19 игроков", 19, ContractType.KILL, 20.0),
            create("Контракт20", "Убить 20 игроков", 20, ContractType.KILL, 20.0),
            create("Контракт21", "Квест на поинты2", 21, ContractType.POINTS, 20.0),
            create("Контракт22", "Квест на поинты3", 22, ContractType.POINTS, 20.0),
            create("Контракт23", "Квест на поинты4", 23, ContractType.POINTS, 20.0),
            create("Контракт25", "Квест на поинты5", 24, ContractType.POINTS, 20.0)
        )
    )

    fun generate() = HashSet(
        listOf<Contract>(
            common.random(),
            common.random(),
            common.random(),
            common.random(),
            common.random(),
            common.random(),
            rare.random(),
            rare.random(),
            rare.random(),
            rare.random(),
            epic.random(),
            epic.random(),
            epic.random(),
            legendary.random()
        )
    )
}