package ui.component.cards

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import constant.Constants.EMPTY_STRING
import logic.model.HomeFeedResponse
import ui.component.KamelLoader
import ui.theme.cardBg
import util.DateUtils.fromToday
import util.noRippleClickable

/**
 * Created by bggRGjQaUbCoE on 2024/6/12
 */
@Composable
fun AppUpdateCard(
    modifier: Modifier = Modifier,
    data: HomeFeedResponse.Data,
    onViewApp: (String) -> Unit,
    onDownloadApk: (String, String, String, String, String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(cardBg())
            .clickable {
                onViewApp(data.packageName.orEmpty())
            }
            .padding(10.dp)
            .animateContentSize()
    ) {

        val (icon, title, bit, version, time, size, log, download) = createRefs()

        KamelLoader(
            url = data.logo,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(1f)
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                })

        Text(
            text = data.title.orEmpty(),
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(title) {
                    start.linkTo(icon.end)
                    top.linkTo(parent.top)
                },
        )

        Text(
            text = when (data.pkgBitType) {
                1 -> "32位"
                2, 3 -> "64位"
                else -> EMPTY_STRING
            },
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(bit) {
                    start.linkTo(title.end)
                    bottom.linkTo(title.bottom)
                    top.linkTo(title.top)
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = run {
               /* if (data.localVersionName.isNullOrEmpty()) {
                    val ver = getAppVersion(context, data.packageName.orEmpty())
                    data.localVersionName = ver.first
                    data.localVersionCode = ver.second
                }*/
                "${data.localVersionName}(${data.localVersionCode}) > ${data.apkversionname}(${data.apkversioncode})"
            },
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(version) {
                    start.linkTo(icon.end)
                    top.linkTo(title.bottom)
                    end.linkTo(download.start)
                    width = Dimension.fillToConstraints
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = fromToday(data.lastupdate ?: 0),
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(time) {
                    start.linkTo(icon.end)
                    top.linkTo(version.bottom)
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = data.apksize.orEmpty(),
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(size) {
                    start.linkTo(time.end)
                    top.linkTo(version.bottom)
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Text(
            text = data.changelog.orEmpty(),
            modifier = Modifier
                .padding(start = 10.dp)
                .constrainAs(log) {
                    start.linkTo(icon.end)
                    top.linkTo(time.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .noRippleClickable {
                    expanded = !expanded
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
        )

        FilledTonalButton(
            onClick = {
                onDownloadApk(
                    data.packageName.orEmpty(),
                    data.id.orEmpty(),
                    data.title.orEmpty(),
                    data.apkversionname.orEmpty(),
                    data.apkversioncode.orEmpty()
                )
            },
            modifier = Modifier
                .constrainAs(download) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        ) {
            Text(text = "下载")
        }

    }

}