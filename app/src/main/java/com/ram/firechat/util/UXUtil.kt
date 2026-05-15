package com.ram.firechat.util

import androidx.compose.ui.graphics.Color
import com.ram.firechat.R
import com.ram.firechat.model.AppColors
import com.ram.firechat.model.AvatarModel
import com.ram.firechat.ui.theme.almostBlack
import com.ram.firechat.ui.theme.almostWhite
import com.ram.firechat.ui.theme.bluishBg

object UXUtil {

    fun getColors(): AppColors {
        return AppColors(
            primaryColor = Color.Black,
            secondaryColor = Color.White,
            textColor = almostWhite,
            primaryAccentColor = almostBlack,
            secondaryAccentColor = bluishBg
        )
    }

    fun getAvatarList(): List<AvatarModel> {
        return listOf(
            AvatarModel(
                avatar = R.drawable.mask256,
                imgIndex = 1
            ),
            AvatarModel(
                avatar = R.drawable.brightness256,
                imgIndex = 2
            ),
            AvatarModel(
                avatar = R.drawable.icecrystal256,
                imgIndex = 3
            ),
            AvatarModel(
                avatar = R.drawable.robo_face,
                imgIndex = 4
            ),
            AvatarModel(
                avatar = R.drawable.robo_round,
                imgIndex = 5
            ),
            AvatarModel(
                avatar = R.drawable.android_bug,
                imgIndex = 6
            ),
            AvatarModel(
                avatar = R.drawable.ar_display,
                imgIndex = 7
            ),
            AvatarModel(
                avatar = R.drawable.star_man,
                imgIndex = 8
            ),
            AvatarModel(
                avatar = R.drawable.spider,
                imgIndex = 9
            ),
//            AvatarModel(
//                avatar = R.drawable.holloween,
//                imgIndex = 10
//            )
        )
    }
}