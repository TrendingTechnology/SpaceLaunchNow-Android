package me.calebjones.spacelaunchnow.ui.main.news.web;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.content.data.articles.ArticleRepository;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;

public class WebNewsFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private Context context;
    private ArticleRepository articleRepository;
    private ArticleAdapter articleAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RealmList<Article> articles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        articleRepository = new ArticleRepository(context);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);


        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        articleAdapter = new ArticleAdapter(context, getActivity());
        recyclerView.setAdapter(articleAdapter);
        getArticles(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getArticles(true);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);

        if (SupporterHelper.isSupporter()) {
            menu.removeItem(R.id.action_supporter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void getArticles(boolean forced) {
        swipeRefreshLayout.setRefreshing(true);
        articleRepository.getArticles(forced, new ArticleRepository.GetArticlesCallback() {
            @Override
            public void onSuccess(RealmList<Article> newArticles) {
                articles = newArticles;
                articleAdapter.addItems(articles);
                swipeRefreshLayout.setRefreshing(false);
                int index = 0;
                for (Article article : articles) {
                    if (article.getMediaUrl() == null) {
                        TextCrawler textCrawler = new TextCrawler();
                        int finalIndex = index;
                        LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                            @Override
                            public void onPre() {

                            }

                            @Override
                            public void onPos(SourceContent sourceContent, boolean b) {
                                if (sourceContent.getImages() != null && sourceContent.getImages().size() > 0) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.executeTransaction(realm1 -> {
                                        article.setMediaUrl(sourceContent.getImages().get(0));
                                        realm1.copyToRealmOrUpdate(article);
                                    });
                                    realm.close();
                                    articleAdapter.updateItem(article, finalIndex);
                                }
                            }
                        };
                        textCrawler.makePreview(linkPreviewCallback, article.getLink(), 1);
                    }
                    index++;
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNetworkFailure() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
