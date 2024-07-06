package ui.component.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import constant.Constants.EMPTY_STRING
import ui.component.KamelLoader
import util.DeviceUtil
import util.decode

/**
 * Created by bggRGjQaUbCoE on 2024/6/10
 */
@Composable
fun MessageHeaderCard(
    modifier: Modifier = Modifier,
    isLogin: Boolean,
    userAvatar: String,
    userName: String,
    level: String,
    experience: String,
    nextLevelExperience: String,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onViewUser: (String) -> Unit
) {

    val animatedProgress by animateFloatAsState(
        targetValue = (experience.toFloatOrNull() ?: 0f) / (nextLevelExperience.toFloatOrNull()
            ?: 1f),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = EMPTY_STRING
    )

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        val startGuide = createGuidelineFromStart(0.75f)

        val (login, avatar, username, levelView, experienceView, indicator, logout) = createRefs()

        if (!isLogin) {
            FilledTonalButton(
                onClick = { onLogin() },
                modifier = Modifier
                    .constrainAs(login) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Text(text = "点击登录")
            }
        }

        if (isLogin) {
            KamelLoader(
                url = userAvatar,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .constrainAs(avatar) {
                        start.linkTo(parent.start)
                        top.linkTo(username.top)
                        bottom.linkTo(indicator.bottom)
                        height = Dimension.fillToConstraints
                    }
                    .clickable { onViewUser(DeviceUtil.uid) }
            )
        }

        Text(
            text = userName.decode,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(username) {
                    start.linkTo(avatar.end)
                    end.linkTo(if (isLogin) logout.start else parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = "Lv.${level}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
            ),
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(levelView) {
                    start.linkTo(avatar.end)
                    top.linkTo(username.bottom)
                }
                .alpha(if (isLogin) 1f else 0f)
        )

        Text(
            text = "${experience}/${nextLevelExperience}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
            ),
            modifier = Modifier
                .constrainAs(experienceView) {
                    top.linkTo(username.bottom)
                    end.linkTo(startGuide)
                }
                .alpha(if (isLogin) 1f else 0f)
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(indicator) {
                    start.linkTo(avatar.end)
                    top.linkTo(levelView.bottom)
                    end.linkTo(startGuide)
                    width = Dimension.fillToConstraints
                }
                .alpha(if (isLogin) 1f else 0f)
        )

        if (isLogin) {
            IconButton(
                onClick = {
                    onLogout()
                },
                modifier = Modifier
                    .constrainAs(logout) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(
                    Icons.AutoMirrored.Default.Logout,
                    contentDescription = null
                )
            }
        }

    }

}