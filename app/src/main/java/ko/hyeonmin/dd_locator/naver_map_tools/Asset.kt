package ko.hyeonmin.dd_locator.naver_bldMap_tools

/**
 * Created by Hyeonmin on 2017-07-05.
 */
class Asset {

    var insertOrModify: Boolean = true
    var bldIdx: String = ""
    var bldType: String = ""
    var platPlc: String = ""
    var newPlatPlc: String = ""

    var mainPurps: String = ""
    var etcPurps: String = ""
    var grndFlrCnt: String = ""
    var useaprDay: String = ""
    var fmlyCnt: String = ""

    var bldName: String = ""
    var bldMemo: String = ""
    var bldIpkey: String = ""
    var bldRoomkey: String = ""

    var bldGwan: String = ""
    var bldTelOwner: String = ""
    var bldTelGwan: String = ""

    var bldMapX: String = ""
    var bldMapY: String = ""

    var bldOnWall: String = ""
    var bldOnParked: String = ""
    var workRequested: String = ""

    var photo: String = ""

    constructor(
            _bld_map_x: String, _bld_map_y: String,
            _plat_plc: String, _new_plat_plc: String,
            _bldType: String
    ) {
        insertOrModify = true
        bldMapX = _bld_map_x
        bldMapY = _bld_map_y
        platPlc = _plat_plc
        newPlatPlc = _new_plat_plc
        bldType = _bldType.replace("all", "one")
    }

    constructor(
            _bld_idx: String,
            _bldType: String,
            _plat_plc: String, _new_plat_plc: String,
            _main_purps: String, _etc_purps: String,
            _grnd_flr_cnt: String, _useapr_day: String, _fmly_cnt: String,
            _bld_name: String, _bld_memo: String,
            _bld_ipkey: String, _bld_roomkey: String,
            _bld_gwan: String, _bld_tel_owner: String, _bld_tel_gwan: String,
            _bld_map_x: String, _bld_map_y: String,
            _bld_on_wall: String, _bld_on_parked: String, _work_reuested: String,
            _photo: String
    ) {
        insertOrModify = false

        bldIdx = _bld_idx
        bldType = _bldType.replace("all", "one")
        platPlc = _plat_plc
        newPlatPlc = _new_plat_plc

        mainPurps = _main_purps
        etcPurps = _etc_purps
        grndFlrCnt = _grnd_flr_cnt
        useaprDay = _useapr_day
        fmlyCnt = _fmly_cnt

        bldName = _bld_name
        bldMemo = _bld_memo
        bldIpkey = _bld_ipkey
        bldRoomkey = _bld_roomkey

        bldGwan = _bld_gwan
        bldTelOwner = _bld_tel_owner
        bldTelGwan = _bld_tel_gwan

        bldMapX = _bld_map_x
        bldMapY = _bld_map_y

        bldOnWall = _bld_on_wall
        bldOnParked = _bld_on_parked
        workRequested = _work_reuested

        photo = _photo;
    }

}