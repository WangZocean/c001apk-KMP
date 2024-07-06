package ui.component.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.launch
import logic.model.HomeFeedResponse
import ui.component.KamelLoader
import ui.theme.cardBg

/**
 * Created by bggRGjQaUbCoE on 2024/6/6
 */
@Composable
fun IconMiniScrollCard(
    modifier: Modifier = Modifier,
    data: HomeFeedResponse.Data,
    onOpenLink: (String, String?) -> Unit
) {

    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LazyRow(
        state = scrollState,
        modifier = modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { detail ->
                    scope.launch {
                        scrollState.scrollBy(-detail)
                    }
                }
            ),
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!data.title.isNullOrEmpty()) {
            item(key = "title") {
                Text(
                    text = data.title,
                    fontSize = 15.sp
                )
            }
        }

        data.entities?.forEach {
            item(key = it.id) {
                IconMiniScrollCardItem(
                    isFeedContent = true,
                    logoUrl = it.logo ?: it.pic.orEmpty(),
                    linkUrl = it.url.orEmpty(),
                    titleText = it.title.orEmpty(),
                    onOpenLink = onOpenLink
                )
            }
        }

    }

}

@Composable
fun IconMiniScrollCardItem(
    modifier: Modifier = Modifier,
    isFeedContent: Boolean,
    logoUrl: String,
    linkUrl: String,
    titleText: String,
    onOpenLink: (String, String?) -> Unit,
    isGridCard: Boolean = false,
) {

    ConstraintLayout(
        modifier = modifier
            .clip(if (isGridCard) RectangleShape else RoundedCornerShape(8.dp))
            .background(
                if (isFeedContent) cardBg()
                else MaterialTheme.colorScheme.surface
            )
            .clickable {
                onOpenLink(linkUrl, titleText)
            }
            .padding(start = if (isGridCard) 10.dp else 5.dp, end = 5.dp)
            .padding(vertical = 5.dp)
    ) {
        val (logo, title) = createRefs()

        KamelLoader(
            url = logoUrl,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .aspectRatio(1f)
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    top.linkTo(title.top)
                    bottom.linkTo(title.bottom)
                    height = Dimension.fillToConstraints
                },
        )

        Text(
            text = titleText,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp)
                .constrainAs(title) {
                    start.linkTo(logo.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    if (isGridCard) {
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

}