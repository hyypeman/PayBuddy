package com.judahben149.emvsync.domain.mapper

import com.judahben149.emvsync.domain.model.card.CardInfo
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity

fun CardInfoEntity.toCardInfo(): CardInfo {
    return CardInfo(
        cardNo = this.cardNo,
        cardExistSlot = this.cardExistslot,
        rfCardType = this.rfCardType,
        isICC = this.isICC,
        csn = this.csn,
        tk1 = this.tk1,
        tk2 = this.tk2,
        tk3 = this.tk3,
        expiredDate = this.expiredDate,
        serviceCode = this.serviceCode,
        tk1ErrInfo = this.tk1ErrInfo,
        tk2ErrInfo = this.tk2ErrInfo,
        tk3ErrInfo = this.tk3ErrInfo,
    )
}