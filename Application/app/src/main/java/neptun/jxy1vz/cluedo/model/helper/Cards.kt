package neptun.jxy1vz.cluedo.model.helper

import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.*

val playerCards = listOf(
    PlayerCard(
        0,
        "Ginny Weasley",
        R.drawable.szereplo_ginny,
        R.drawable.szereplo_hatlap,
        Sex.WOMAN,
        PlayerColor.BLUE
    ),
    PlayerCard(
        1,
        "Harry Potter",
        R.drawable.szereplo_harry,
        R.drawable.szereplo_hatlap,
        Sex.MAN,
        PlayerColor.PURPLE
    ),
    PlayerCard(
        2,
        "Hermione Granger",
        R.drawable.szereplo_hermione,
        R.drawable.szereplo_hatlap,
        Sex.WOMAN,
        PlayerColor.RED
    ),
    PlayerCard(
        3,
        "Ron Weasley",
        R.drawable.szereplo_ron,
        R.drawable.szereplo_hatlap,
        Sex.MAN,
        PlayerColor.YELLOW
    ),
    PlayerCard(
        4,
        "Luna Lovegood",
        R.drawable.szereplo_luna,
        R.drawable.szereplo_hatlap,
        Sex.WOMAN,
        PlayerColor.WHITE
    ),
    PlayerCard(
        5,
        "Neville Longbottom",
        R.drawable.szereplo_neville,
        R.drawable.szereplo_hatlap,
        Sex.MAN,
        PlayerColor.GREEN
    )
)

var helperCards = listOf(
    HelperCard(
        0,
        "Bezoár",
        R.drawable.mento_bezoar,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        1,
        "Seprű",
        R.drawable.mento_sepru,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        2,
        "Álságdetektor",
        R.drawable.mento_alsagdetektor,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        3,
        "Önoltó",
        R.drawable.mento_onolto,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        4,
        "Varangydudva",
        R.drawable.mento_varangydudva,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        5,
        "Láthatatlanná tévő köpeny",
        R.drawable.mento_lathatatlanna_tevo_kopeny,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        6,
        "Mandragórás gyógyszirup",
        R.drawable.mento_mandragoras_gyogyszirup,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        7,
        "Tekergők térképe",
        R.drawable.mento_tekergok_terkepe,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        8,
        "Zsupszkulcs",
        R.drawable.mento_zsupszkulcs,
        R.drawable.mento_hatlap,
        HelperType.TOOL
    ),
    HelperCard(
        9,
        "Felix Felicis",
        R.drawable.mento_felix_felicis,
        R.drawable.mento_hatlap,
        HelperType.TOOL,
        3,
        "12-es dobás"
    ),
    HelperCard(
        10,
        "Albus Dumbledore",
        R.drawable.mento_albus_dumbledore,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        11,
        "Dobby",
        R.drawable.mento_dobby,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        12,
        "Fawkes",
        R.drawable.mento_fawkes,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        13,
        "Madam Pomfrey",
        R.drawable.mento_madam_pomfrey,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        14,
        "McGalagony professzor",
        R.drawable.mento_mcgalagony_professzor,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        15,
        "Piton professzor",
        R.drawable.mento_piton_professzor,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        16,
        "Rubeus Hagrid",
        R.drawable.mento_rubeus_hagrid,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        17,
        "Weasley ikrek",
        R.drawable.mento_weasley_ikrek,
        R.drawable.mento_hatlap,
        HelperType.ALLY
    ),
    HelperCard(
        18,
        "Capitulatus",
        R.drawable.mento_capitulatus,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        19,
        "Immobilus",
        R.drawable.mento_immobilus,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        20,
        "Lumos",
        R.drawable.mento_lumos,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        21,
        "Petrificus Totalus",
        R.drawable.mento_petrificus_totalus,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        22,
        "Protego",
        R.drawable.mento_protego,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        23,
        "Commikulissimus",
        R.drawable.mento_commikulissimus,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        24,
        "Stupor",
        R.drawable.mento_stupor,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        25,
        "Vingardium Leviosa",
        R.drawable.mento_vingardium_leviosa,
        R.drawable.mento_hatlap,
        HelperType.SPELL
    ),
    HelperCard(
        26,
        "Alohomora",
        R.drawable.mento_alohomora,
        R.drawable.mento_hatlap,
        HelperType.SPELL,
        5,
        "Zárt ajtón bemenni"
    )
).toMutableList()

var mysteryCards = listOf(
    MysteryCard(
        0,
        "Altatóital",
        R.drawable.rejtely_altatoital,
        R.drawable.rejtely_hatlap,
        MysteryType.TOOL
    ),
    MysteryCard(
        1,
        "Mandragóra",
        R.drawable.rejtely_mandragora,
        R.drawable.rejtely_hatlap,
        MysteryType.TOOL
    ),
    MysteryCard(
        2,
        "Impedimenta Obstructo",
        R.drawable.rejtely_obstructo,
        R.drawable.rejtely_hatlap,
        MysteryType.TOOL
    ),
    MysteryCard(
        3,
        "Petrificus Totalus",
        R.drawable.rejtely_petrificus_totalus,
        R.drawable.rejtely_hatlap,
        MysteryType.TOOL
    ),
    MysteryCard(
        4,
        "Volt-nincs szekrény",
        R.drawable.rejtely_volt_nincs,
        R.drawable.rejtely_hatlap,
        MysteryType.TOOL
    ),
    MysteryCard(
        5,
        "Zsupszkulcs",
        R.drawable.rejtely_zsupszkulcs,
        R.drawable.rejtely_hatlap,
        MysteryType.TOOL
    ),
    MysteryCard(
        6,
        "Bellatrix Lestrange",
        R.drawable.rejtely_bellatrix_lestrange,
        R.drawable.rejtely_hatlap,
        MysteryType.SUSPECT
    ),
    MysteryCard(
        7,
        "Crak és Monstro",
        R.drawable.rejtely_crak_es_monstro,
        R.drawable.rejtely_hatlap,
        MysteryType.SUSPECT
    ),
    MysteryCard(
        8,
        "Dolores Umbridge",
        R.drawable.rejtely_dolores_umbridge,
        R.drawable.rejtely_hatlap,
        MysteryType.SUSPECT
    ),
    MysteryCard(
        9,
        "Draco Malfoy",
        R.drawable.rejtely_draco_malfoy,
        R.drawable.rejtely_hatlap,
        MysteryType.SUSPECT
    ),
    MysteryCard(
        10,
        "Lucius Malfoy",
        R.drawable.rejtely_lucius_malfoy,
        R.drawable.rejtely_hatlap,
        MysteryType.SUSPECT
    ),
    MysteryCard(
        11,
        "Peter Pettigrew",
        R.drawable.rejtely_peter_pettigrew,
        R.drawable.rejtely_hatlap,
        MysteryType.SUSPECT
    ),
    MysteryCard(
        12,
        "Bagolyház",
        R.drawable.rejtely_bagolyhaz,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        13,
        "Bájitaltan terem",
        R.drawable.rejtely_bajitaltan,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        14,
        "Gyengélkedő",
        R.drawable.rejtely_gyengelkedo,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        15,
        "Jóslástan terem",
        R.drawable.rejtely_joslastan,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        16,
        "Könyvtár",
        R.drawable.rejtely_konyvtar,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        17,
        "Nagyterem",
        R.drawable.rejtely_nagyterem,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        18,
        "Serleg terem",
        R.drawable.rejtely_serleg,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        19,
        "Sötét varázslatok kivédése terem",
        R.drawable.rejtely_svk,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    ),
    MysteryCard(
        20,
        "Szükség szobája",
        R.drawable.rejtely_szukseg_szobaja,
        R.drawable.rejtely_hatlap,
        MysteryType.VENUE
    )
).toMutableList()

var darkCards = listOf(
    DarkCard(
        0,
        "Különös csomagot kapsz: a tartalmát megátkozták.",
        R.drawable.sotet_pakli_corridor_1,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        15,
        listOf(13, 16, 2)
    ),
    DarkCard(
        1,
        "Valaki megmérgezte a Vajsörödet... nem érzed jól magad.",
        R.drawable.sotet_pakli_corridor_2,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        10,
        listOf(0, 13)
    ),
    DarkCard(
        2,
        "Kővé dermesztettek.",
        R.drawable.sotet_pakli_corridor_3,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        20,
        listOf(6, 13)
    ),
    DarkCard(
        3,
        "Miközben elhaladsz az udvari szökőkút mellett, a kiemelkedő vízoszlop foglyul ejt és nem kapsz levegőt.",
        R.drawable.sotet_pakli_corridor_4,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        10,
        listOf(4, 10)
    ),
    DarkCard(
        4,
        "Egy Mumus annak a képében közeledik, amitől a legjobban félsz...",
        R.drawable.sotet_pakli_corridor_5,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        20,
        listOf(1, 8, 23)
    ),
    DarkCard(
        5,
        "A klubhelyiségbe nem tudsz bemenni.",
        R.drawable.sotet_pakli_corridor_6,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        5,
        listOf(10)
    ),
    DarkCard(
        6,
        "Kábító átok fog sújtani hamarosan.",
        R.drawable.sotet_pakli_corridor_7,
        R.drawable.sotet_pakli_hatlap,
        DarkType.CORRIDOR,
        LossType.HP,
        15,
        listOf(18, 21, 22, 24)
    ),
    DarkCard(
        7,
        "Egy bébi Mandragórát találsz a táskádban - és éppen felébredt. Nincs rajtad fülvédő.",
        R.drawable.sotet_pakli_picker_1,
        R.drawable.sotet_pakli_hatlap,
        DarkType.PLAYER_IN_TURN,
        LossType.HP,
        10,
        listOf(13, 15)
    ),
    DarkCard(
        8,
        "Befordulsz a sarkon és egyenesen egy Ördöghurok bozótba esel.",
        R.drawable.sotet_pakli_picker_2,
        R.drawable.sotet_pakli_hatlap,
        DarkType.PLAYER_IN_TURN,
        LossType.HP,
        15,
        listOf(20, 8)
    ),
    DarkCard(
        9,
        "Valaki a közelben Önoltót használt, sötétben tapogatózol.",
        R.drawable.sotet_pakli_picker_3,
        R.drawable.sotet_pakli_hatlap,
        DarkType.PLAYER_IN_TURN,
        LossType.HP,
        15,
        listOf(20, 8)
    ),
    DarkCard(
        10,
        "Az édességedet Rókázó Rágcsára cserélték - és későn vetted észre.",
        R.drawable.sotet_pakli_picker_4,
        R.drawable.sotet_pakli_hatlap,
        DarkType.PLAYER_IN_TURN,
        LossType.HP,
        15,
        listOf(13, 17)
    ),
    DarkCard(
        11,
        "A kviddicsmérkőzés alatt megátkozzák a seprűd.",
        R.drawable.sotet_pakli_picker_5,
        R.drawable.sotet_pakli_hatlap,
        DarkType.PLAYER_IN_TURN,
        LossType.HP,
        20,
        listOf(10, 13, 12)
    ),
    DarkCard(
        12,
        "Sürgős üzenetet kellene kézbesítened, de a baglyod eltűnt.",
        R.drawable.sotet_pakli_bagolyhaz,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        15,
        listOf(16, 17, 11)
    ),
    DarkCard(
        13,
        "Valaki beletett valamit az üstödbe; a füsttől kába leszel.",
        R.drawable.sotet_pakli_bajitaltan,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        25,
        listOf(1, 15)
    ),
    DarkCard(
        14,
        "A Bogoly Berti féle Drazsét megbabrálták. Nagyon lebetegedsz.",
        R.drawable.sotet_pakli_gyengelkedo,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        15,
        listOf(0, 13)
    ),
    DarkCard(
        15,
        "A tea levelek a Zordót mutatják. Pánikba esel.",
        R.drawable.sotet_pakli_joslastan,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        5,
        listOf(14)
    ),
    DarkCard(
        16,
        "Lépéseket hallasz, miközben a Zárolt Részben leemelsz egy könyvet.",
        R.drawable.sotet_pakli_konyvtar,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        10,
        listOf(3, 5, 8)
    ),
    DarkCard(
        17,
        "A Párbajszakkör alatt a Mardekáros ellenfeled egy kígyót varázsol...",
        R.drawable.sotet_pakli_nagyterem,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        15,
        listOf(12, 18, 19, 22)
    ),
    DarkCard(
        18,
        "Felkapsz egy serleget, de későn veszed észre, hogy az egy Zsupszkulcs.",
        R.drawable.sotet_pakli_serleg,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        5
    ),
    DarkCard(
        19,
        "Kelta Tündérmanók egy ártalmas csoportja tart feléd.",
        R.drawable.sotet_pakli_svk,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        20,
        listOf(19, 14, 8)
    ),
    DarkCard(
        20,
        "Egy fal összeomlását hangos robaj kíséri. Valaki közeleg.",
        R.drawable.sotet_pakli_szukseg_szobaja,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ROOM,
        LossType.HP,
        20,
        listOf(3, 5, 1, 8)
    ),
    DarkCard(
        21,
        "Dumbledore elhagyta a kastélyt, pedig információd van a Halálfalókról.",
        R.drawable.sotet_pakli_all_players_1,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.ALLY,
        0
    ),
    DarkCard(
        22,
        "Egyik elvarázsolt osztálytársad egy Főbenjáró Átkot készül rád szórni.",
        R.drawable.sotet_pakli_all_players_2,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        15,
        listOf(21, 22, 24, 5)
    ),
    DarkCard(
        23,
        "A kastélyban elszabadult kis sárkány meglát és tüzet okád rád.",
        R.drawable.sotet_pakli_all_players_3,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        10,
        listOf(16, 5, 12)
    ),
    DarkCard(
        24,
        "Draco Malfoy és barátai kelepcébe csalnak, hogy párbajt kezdhessetek.",
        R.drawable.sotet_pakli_all_players_4,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        5,
        listOf(14, 7, 18, 24)
    ),
    DarkCard(
        25,
        "Dementorok szállták meg az iskolát.",
        R.drawable.sotet_pakli_all_players_5,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.SPELL,
        0
    ),
    DarkCard(
        26,
        "Valaki levette a csatot a Szörnyek Szörnyű Könyvéről és az megtámad.",
        R.drawable.sotet_pakli_all_players_6,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        15,
        listOf(16, 12, 13)
    ),
    DarkCard(
        27,
        "Egy ismert vérfarkas zsákmányt keres a termekben... telihold van.",
        R.drawable.sotet_pakli_all_players_7,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        10,
        listOf(7, 5, 15)
    ),
    DarkCard(
        28,
        "A kastélyban szabadon kószáló, kifejlett hegyi trollal találkozol.",
        R.drawable.sotet_pakli_all_players_8,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        15,
        listOf(25, 7, 1, 21)
    ),
    DarkCard(
        29,
        "Halálfaló Lucius Malfoy meglátogatja a fiát, Dracot.",
        R.drawable.sotet_pakli_all_players_9,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.HP,
        5,
        listOf(7, 5, 10)
    ),
    DarkCard(
        30,
        "Frics úr - a szokásosnál is gorombább hangulatban - befordul a sarkon.",
        R.drawable.sotet_pakli_all_players_10,
        R.drawable.sotet_pakli_hatlap,
        DarkType.ALL_PLAYERS,
        LossType.TOOL,
        0
    ),
    DarkCard(
        31,
        "A Griffendéles fiúk hálótermét kifosztották. Értékes dolgok tűntek el.",
        R.drawable.sotet_pakli_ferfiaknak,
        R.drawable.sotet_pakli_hatlap,
        DarkType.SEX,
        LossType.HP,
        20,
        listOf(14, 10)
    ),
    DarkCard(
        32,
        "Cormac McLaggen szerelmi bájitallal átitatott édességet evett - és csak rád tud gondolni.",
        R.drawable.sotet_pakli_noknek,
        R.drawable.sotet_pakli_hatlap,
        DarkType.SEX,
        LossType.HP,
        20,
        listOf(15, 1)
    )
).toMutableList()