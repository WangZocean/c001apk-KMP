package ui.component.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import logic.model.HomeFeedResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ui.component.KamelLoader
import ui.component.LinkText
import ui.theme.cardBg
import util.DateUtils
import util.ReportType

/**
 * Created by bggRGjQaUbCoE on 2024/6/11
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    data: HomeFeedResponse.Data,
    onViewUser: (String) -> Unit,
    onOpenLink: (String, String?) -> Unit,
    onReport: ((String, ReportType) -> Unit)? = null,
    onDeleteNotice: ((String) -> Unit)? = null,
) {

    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val isFollow by lazy { data.type == "contacts_follow" }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(cardBg())
            .combinedClickable(
                onClick = {
                    if (isFollow) {
                        onOpenLink(data.url.orEmpty(), null)
                    } else {
                        val doc: Document = Jsoup.parse(data.note.orEmpty())
                        val links: Elements = doc.select("a[href]")
                        onOpenLink(
                            links
                                .getOrNull(0)
                                ?.attr("href")
                                .orEmpty(),
                            null
                        )
                    }
                },
                onLongClick = {
                    onDeleteNotice?.let { it(data.id.orEmpty()) }
                }
            )
    ) {

        val (avatar, username, expand, message, dateLine) = createRefs()

        KamelLoader(
            url = data.fromUserAvatar,
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .size(30.dp)
                .clip(CircleShape)
                .constrainAs(avatar) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .clickable {
                    onViewUser(data.fromuid.orEmpty())
                }
        )

        Text(
            text = data.fromusername.orEmpty(),
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                .constrainAs(username) {
                    start.linkTo(avatar.end)
                    top.linkTo(parent.top)
                    end.linkTo(expand.start)
                    width = Dimension.fillToConstraints
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
        )

        LinkText(
            text = data.note.orEmpty(),
            modifier = Modifier
                .padding(start = 10.dp, top = 5.dp, end = 10.dp)
                .constrainAs(message) {
                    start.linkTo(avatar.end)
                    top.linkTo(username.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            onOpenLink = if (isFollow) null else onOpenLink,
        )

        Text(
            text = DateUtils.fromToday(data.dateline ?: 0),
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                .constrainAs(dateLine) {
                    start.linkTo(avatar.end)
                    top.linkTo(message.bottom)
                },
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.outline
        )

        Box(
            modifier = Modifier
                .constrainAs(expand) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }) {

            IconButton(
                onClick = {
                    dropdownMenuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            DropdownMenu(
                expanded = dropdownMenuExpanded,
                onDismissRequest = {
                    dropdownMenuExpanded = false
                },
            ) {
                listOf("Block"/*, "Report"*/).forEachIndexed { index, menu ->
                    DropdownMenuItem(
                        text = { Text(menu) },
                        onClick = {
                            dropdownMenuExpanded = false
                            when (index) {
                                1 -> onReport?.let { it(data.fromuid.orEmpty(), ReportType.USER) }
                            }
                        }
                    )
                }
            }

        }

    }
}