package ui.component.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ui.theme.cardBg
import util.DeviceUtil

/**
 * Created by bggRGjQaUbCoE on 2024/6/10
 */

val backgroundList = listOf(0xFF2196F3, 0xFF00BCD4, 0xFF4CAF50, 0xFFF44336, 0xFFFF9800)
val iconList = listOf(
    Icons.Default.AlternateEmail,
    Icons.AutoMirrored.Outlined.Message,
    Icons.Outlined.ThumbUp,
    Icons.Outlined.PersonAdd,
    Icons.Outlined.Mail
)
val titleList = listOf("@我的动态", "@我的评论", "我收到的赞", "好友关注", "私信")

@Composable
fun MessageListCard(
    modifier: Modifier = Modifier,
    background: Long,
    imageVector: ImageVector,
    title: String,
    count: Int?,
    onViewNotice: () -> Unit,
) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(cardBg())
            .clickable {
                if (DeviceUtil.isLogin) {
                    onViewNotice()
                }
            }
            .padding(10.dp),
    ) {

        val (icon, name, badge, arrow) = createRefs()

        MessageCardLogo(
            background = background,
            imageVector = imageVector,
            modifier = Modifier
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(name) {
                    start.linkTo(icon.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        count?.let {
            if (it > 0) {
                Badge(
                    modifier = Modifier
                        .constrainAs(badge) {
                            start.linkTo(name.start)
                            end.linkTo(arrow.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Text(text = it.toString())
                }
            }
        }

        Image(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .constrainAs(arrow) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

    }

}

@Composable
private fun MessageCardLogo(
    modifier: Modifier = Modifier,
    background: Long,
    imageVector: ImageVector,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(background)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}