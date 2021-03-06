package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";

    public static Fragment createFragment(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);
        return fragment;
    }


    private final int LOADER_ID = 10;

    private View rootView;

    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    private Picasso mPicasso;


    public BookDetail(){
        ean = null;
        bookTitle = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //init Picasso
        mPicasso = Picasso.with(getContext());
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);

                //back only with tablet and if has history
                if (MainActivity.IS_TABLET) {
                    manageTabletStack();
                } else {
                    getActivity().finish();
                }
            }
        });
        return rootView;
    }

    private void manageTabletStack() {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        int bookEntryCount = 0;
        for(int entry = 0; entry < fm.getBackStackEntryCount(); entry++){
            String frag = fm.getBackStackEntryAt(entry).getName();
            if(frag != null && frag.contains(getString(R.string.book_detail))) {
                bookEntryCount++;
            }
        }

        //if use backStack, pop
        if(bookEntryCount > 0) {
            fm.popBackStack();
        }
        //else, only remove the fragment
        else {
            fm.beginTransaction()
                    .remove(BookDetail.this)
                    .commit();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        initShareActionWithData();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);

        initShareActionWithData();

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = (!TextUtils.isEmpty(authors) ? authors.split(",") : null);
        ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr != null ? authorsArr.length : 0);
        ((TextView) rootView.findViewById(R.id.authors)).setText(authors != null ? authors.replace(",","\n") : "");
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            mPicasso.load(imgUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .into((ImageView) rootView.findViewById(R.id.fullBookCover));
            rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
        } else {
            ((ImageView) rootView.findViewById(R.id.fullBookCover)).setImageResource(R.drawable.ic_launcher);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        /*
        //not need : it was replaced by Master/Detail flow
        if(rootView.findViewById(R.id.right_container)!=null){
            rootView.findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
        }*/

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    /*
    //onPause must not call onDestroyView
    //no popBackStack here : cf Master/Detail flow
    @Override
    public void onPause() {
        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }*/


    private void initShareActionWithData() {
        //Init shareAction if all object/data are set
        if (shareActionProvider != null && !TextUtils.isEmpty(bookTitle)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
            shareActionProvider.setShareIntent(shareIntent);
        }
    }
}