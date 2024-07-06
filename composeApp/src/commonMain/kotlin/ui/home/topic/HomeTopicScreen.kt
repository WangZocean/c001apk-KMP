package ui.home.topic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.launch
import logic.model.HomeFeedResponse
import logic.model.TopicBean
import logic.state.LoadingState
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import ui.carousel.CarouselContentScreen
import ui.component.cards.LoadingCard
import ui.home.TabType
import ui.theme.cardBg

/**
 * Created by bggRGjQaUbCoE on 2024/6/11
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTopicScreen(
    childComponentContext: ComponentContext,
    type: TabType,
    onViewUser: (String) -> Unit,
    onViewFeed: (String, Boolean) -> Unit,
    onOpenLink: (String, String?) -> Unit,
    onCopyText: (String?) -> Unit,
    onViewImage: (List<String>, Int) -> Unit,
) {

    val viewModel = childComponentContext.instanceKeeper.getOrCreate(key = type.name) {
        koinInject<HomeTopicViewModel> {
            parametersOf(
                when (type) {
                    TabType.TOPIC -> "/v6/page/dataList?url=V11_VERTICAL_TOPIC&title=话题&page=1"
                    TabType.PRODUCT -> "/v6/product/categoryList"
                    else -> throw IllegalArgumentException("invalid type: $type")
                }
            )
        }
    }

    val scope = rememberCoroutineScope()
    val currentIndex = when (type) {
        TabType.TOPIC -> 1
        TabType.PRODUCT -> 0
        else -> throw IllegalArgumentException("invalid type: $type")
    }
    val listState = rememberLazyListState()
    var pageState: PagerState
    var tabList: List<TopicBean>?

    Box(modifier = Modifier.fillMaxSize()) {
        when (viewModel.loadingState) {
            LoadingState.Loading, LoadingState.Empty, is LoadingState.Error -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoadingCard(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 10.dp),
                        state = viewModel.loadingState,
                        onClick = if (viewModel.loadingState is LoadingState.Loading) null
                        else viewModel::loadMore
                    )
                }
            }

            is LoadingState.Success -> {

                tabList = when (type) {
                    TabType.TOPIC -> (viewModel.loadingState as LoadingState.Success<List<HomeFeedResponse.Data>>)
                        .response.getOrNull(0)?.entities?.map {
                            TopicBean(it.url.orEmpty(), it.title.orEmpty())
                        }

                    TabType.PRODUCT -> (viewModel.loadingState as LoadingState.Success<List<HomeFeedResponse.Data>>)
                        .response.map {
                            TopicBean(it.url.orEmpty(), it.title.orEmpty())
                        }

                    else -> throw IllegalArgumentException("invalid type: $type")
                }

                tabList?.let {
                    pageState = rememberPagerState(
                        initialPage = currentIndex,
                        pageCount = {
                            it.size
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(cardBg())
                    ) {

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.22f)
                        ) {
                            itemsIndexed(it, key = { _, item -> item.title }) { index, item ->
                                Row(
                                    modifier = Modifier
                                        .height(IntrinsicSize.Min)
                                        .fillMaxWidth()
                                        .clickable {
                                            scope.launch {
                                                pageState.scrollToPage(index)
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(3.dp)
                                            .background(
                                                if (index == pageState.currentPage) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surface
                                            )
                                    )
                                    Text(
                                        text = item.title,
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (index == pageState.currentPage)
                                                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        3.dp
                                                    )
                                                else MaterialTheme.colorScheme.surface
                                            )
                                            .padding(8.dp)
                                            .align(Alignment.CenterVertically),
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp,
                                        color = if (index == pageState.currentPage) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        VerticalPager(
                            state = pageState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.78f),
                            userScrollEnabled = false,
                        ) { index ->
                            CarouselContentScreen(
                                childComponentContext = childComponentContext,
                                url = it[index].url,
                                title = it[index].title,
                                paddingValues = PaddingValues(),
                                refreshState = null,
                                resetRefreshState = {},
                                onViewUser = onViewUser,
                                onViewFeed = onViewFeed,
                                onOpenLink = onOpenLink,
                                onCopyText = onCopyText,
                                isHomeFeed = true,
                                onViewImage = onViewImage,
                            )
                        }

                    }
                }
            }
        }
    }

}