package ui.component.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import constant.Constants.PREFIX_HTTP
import logic.model.HomeFeedResponse
import ui.component.KamelLoader
import ui.component.LinkText
import ui.theme.cardBg
import util.getImageLp
import util.longClick

/**
 * Created by bggRGjQaUbCoE on 2024/6/19
 */
@Composable
fun ChatLeftCard(
    modifier: Modifier = Modifier,
    data: HomeFeedResponse.Data,
    onGetImageUrl: (String) -> Unit,
    onLongClick: (String, String, String) -> Unit,
    onViewUser: (String) -> Unit,
    onViewImage: (List<String>, Int) -> Unit,
) {

    BoxWithConstraints {
        val maxWidth = maxWidth - 142.dp

        Row(
            modifier = modifier
                .fillMaxWidth()
                .longClick {
                    onLongClick(
                        data.id.orEmpty(),
                        data.message.orEmpty(),
                        data.messagePic.orEmpty()
                    )
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {

            KamelLoader(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .clickable { onViewUser(data.messageUid.orEmpty()) },
                url = data.fromUserAvatar
            )

            if (!data.message.isNullOrEmpty()) {
                LinkText(
                    text = data.message,
                    modifier = Modifier
                        .widthIn(max = maxWidth)
                        .padding(start = 10.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 12.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp
                            )
                        )
                        .background(cardBg())
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                )
            }

            if (!data.messagePic.isNullOrEmpty()) {
                if (!data.messagePic.startsWith(PREFIX_HTTP)) {
                    onGetImageUrl(data.id.orEmpty())
                }
                val imageLp by lazy {
                    getImageLp(
                        if (data.messagePic.startsWith(PREFIX_HTTP))
                            data.messagePic.substring(0, data.messagePic.indexOfFirst { it == '?' })
                        else data.messagePic
                    )
                }
                val imageWidth by lazy { maxWidth / 2f }
                val imageHeight by lazy { imageWidth * imageLp.second / imageLp.first }
                KamelLoader(
                    url = data.messagePic,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(cardBg())
                        .width(maxWidth / 2f)
                        .height(imageHeight)
                        .clickable {
                            onViewImage(listOf(data.messagePic), 0)
                        },
                    isChat = true,
                )
            }
        }
    }

}