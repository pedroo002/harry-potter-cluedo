package neptun.jxy1vz.cluedo.model.helper

import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.model.Door
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.Position
import neptun.jxy1vz.cluedo.model.Room

var playerList = listOf(
    Player(0, Position(0, 17), R.id.ivBluePlayer),
    Player(1, Position(24, 17), R.id.ivPurplePlayer),
    Player(2, Position(0, 7), R.id.ivRedPlayer),
    Player(3, Position(24, 7), R.id.ivYellowPlayer),
    Player(4, Position(17, 24), R.id.ivWhitePlayer),
    Player(5, Position(7, 0), R.id.ivGreenPlayer)
).toMutableList()

val playerImageIdList = listOf(
    R.id.ivBluePlayer,
    R.id.ivPurplePlayer,
    R.id.ivRedPlayer,
    R.id.ivYellowPlayer,
    R.id.ivWhitePlayer,
    R.id.ivGreenPlayer
).toMutableList()

val roomList = listOf(
    Room(0, 0, 6, 5, 0, 42, R.drawable.selection_room_sotet_varazslatok_kivedese),
    Room(1, 0, 15, 6, 9, 49, R.drawable.selection_room_nagyterem),
    Room(2, 0, 24, 6, 18, 49, R.drawable.selection_room_gyengelkedo),
    Room(3, 8, 6, 11, 0, 28, R.drawable.selection_room_konyvtar),
    Room(4, 10, 14, 15, 10, 30, R.drawable.selection_room_dumbledore),
    Room(5, 9, 24, 15, 18, 49, R.drawable.selection_room_szukseg_szobaja),
    Room(6, 13, 6, 16, 0, 28, R.drawable.selection_room_bagolyhaz),
    Room(7, 19, 6, 24, 0, 42, R.drawable.selection_room_joslastan_terem),
    Room(8, 18, 15, 24, 9, 49, R.drawable.selection_room_serleg_terem),
    Room(9, 18, 24, 24, 18, 49, R.drawable.selection_room_bajitaltan_terem)
)

val doorList = listOf(
    Door(Position(1, 7), roomList[0]),
    Door(Position(6, 1), roomList[0]),
    Door(Position(5, 8), roomList[1]),
    Door(Position(7, 12), roomList[1]),
    Door(Position(5, 16), roomList[1]),
    Door(Position(1, 17), roomList[2]),
    Door(Position(7, 23), roomList[2]),
    Door(Position(8, 7), roomList[3]),
    Door(Position(12, 2), roomList[3]),
    Door(Position(11, 9), roomList[4]),
    Door(Position(14, 9), roomList[4]),
    Door(Position(13, 15), roomList[4]),
    Door(Position(8, 19), roomList[5]),
    Door(Position(16, 19), roomList[5]),
    Door(Position(12, 4), roomList[6]),
    Door(Position(16, 7), roomList[6]),
    Door(Position(18, 2), roomList[7]),
    Door(Position(22, 7), roomList[7]),
    Door(Position(17, 12), roomList[8]),
    Door(Position(19, 8), roomList[8]),
    Door(Position(19, 16), roomList[8]),
    Door(Position(17, 22), roomList[9]),
    Door(Position(21, 17), roomList[9])
)

val starList = arrayOf(
    Position(2, 8),
    Position(8, 10),
    Position(8, 23),
    Position(14, 17),
    Position(16, 11),
    Position(18, 16),
    Position(13, 19),
    Position(23, 16)
)

val cols = arrayOf(
    R.id.borderLeft,
    R.id.guidelineCol1,
    R.id.guidelineCol2,
    R.id.guidelineCol3,
    R.id.guidelineCol4,
    R.id.guidelineCol5,
    R.id.guidelineCol6,
    R.id.guidelineCol7,
    R.id.guidelineCol8,
    R.id.guidelineCol9,
    R.id.guidelineCol10,
    R.id.guidelineCol11,
    R.id.guidelineCol12,
    R.id.guidelineCol13,
    R.id.guidelineCol14,
    R.id.guidelineCol15,
    R.id.guidelineCol16,
    R.id.guidelineCol17,
    R.id.guidelineCol18,
    R.id.guidelineCol19,
    R.id.guidelineCol20,
    R.id.guidelineCol21,
    R.id.guidelineCol22,
    R.id.guidelineCol23,
    R.id.guidelineCol24,
    R.id.borderRight
)
val rows = arrayOf(
    R.id.borderTop,
    R.id.guidelineRow1,
    R.id.guidelineRow2,
    R.id.guidelineRow3,
    R.id.guidelineRow4,
    R.id.guidelineRow5,
    R.id.guidelineRow6,
    R.id.guidelineRow7,
    R.id.guidelineRow8,
    R.id.guidelineRow9,
    R.id.guidelineRow10,
    R.id.guidelineRow11,
    R.id.guidelineRow12,
    R.id.guidelineRow13,
    R.id.guidelineRow14,
    R.id.guidelineRow15,
    R.id.guidelineRow16,
    R.id.guidelineRow17,
    R.id.guidelineRow18,
    R.id.guidelineRow19,
    R.id.guidelineRow20,
    R.id.guidelineRow21,
    R.id.guidelineRow22,
    R.id.guidelineRow23,
    R.id.guidelineRow24,
    R.id.borderBottom
)