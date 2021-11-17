# Tech News App

The Tech News App will fetch several articles from [hacker news](https://hacker-news.firebaseio.com/v0/topstories.json) and then display them in a ListView. The clicked article will be displayed in a new activity showing its webview

## Steps

1. Fetch 20 articles maximum from the following API:
[hacker news](https://hacker-news.firebaseio.com/v0/topstories.json)
2. For each article ID retrieved from the above API, execute
the following API: [hackernews.firebase.com](https://hackernews.firebaseio.com/v0/item/[ARTICLE_ID].json?print=pretty) where [ARTICLE_ID] will be the article ID retrieved from
the above API. The API will let you access details about
this article. We are only interested in Article Title and
Article URL. Parse the article URL to store the html code
of the URL in question in a String variable.
3. Now that you have the article ID, article Name, and article
content (html source code), save all these information into
an SQLite database. Next time you load the app, the above
information should now be retrieved from the local database.
4. Select all the articles from the database and display them
in the List View.
5. On click on one article, a new activity will be loaded and
the WebView should show the content (html source code) of
the clicked article. 

