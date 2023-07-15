package com.judahben149.emvsync.domain.model.card

import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.device.reader.RfCardTypeEnum
import com.nexgo.oaf.apiv3.device.reader.TrackErrorEnum

data class CardInfo(
    var cardNo: String? = null,
    val cardExistSlot: CardSlotTypeEnum? = null,
    val rfCardType: RfCardTypeEnum? = null,
    val isICC: Boolean = false,
    val csn: String? = null,
    val tk1: String? = null,
    val tk2: String? = null,
    val tk3: String? = null,
    val expiredDate: String? = null,
    val serviceCode: String? = null,
    val tk1ErrInfo: TrackErrorEnum? = null,
    val tk2ErrInfo: TrackErrorEnum? = null,
    val tk3ErrInfo: TrackErrorEnum? = null
)

enum class CardType {
    ICC,
    SWIPE,
    RF
}
