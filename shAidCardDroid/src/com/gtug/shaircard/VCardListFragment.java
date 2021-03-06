package com.gtug.shaircard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gtug.shaircard.model.Event;
import com.gtug.shaircard.model.VCard;
import com.stanfy.app.fragments.list.FetchingListFragment;
import com.stanfy.images.ImagesManagerContext;
import com.stanfy.views.LoadableImageView;
import com.stanfy.views.list.Fetcher;
import com.stanfy.views.list.ModelListAdapter.ElementRenderer;
import com.stanfy.views.list.PageFetcher;

public class VCardListFragment extends
		FetchingListFragment<shAirCardApp, VCard> {

	Long eventId;

	public VCardListFragment(Event event) {
		eventId = event.getId();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RENDERER = createRenderer(getOwnerActivity().getApp());

		// getListView().setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> view, View arg1,
		// int position, long arg3) {
		// Event event = (Event) view.getItemAtPosition(position);
		//
		// }
		// });

	}

	static class VcardHolder {
		LoadableImageView imageView;
		TextView name;
		TextView company;
		TextView phone;
	}

	public static ElementRenderer<VCard> createRenderer(final shAirCardApp app) {
		return new ElementRenderer<VCard>(R.layout.vcard_item) {
			@Override
			public void render(final Adapter adapter, final ViewGroup parent,
					final VCard element, final View view, final Object holder,
					final int position) {
				final VcardHolder h = (VcardHolder) holder;
				h.name.setText(element.getFirstName() + " "
						+ element.getSurname());
				h.company.setText(element.getCompany());
				h.phone.setText(element.getPhone());
				// TODO get image uri

				if (element.getId() != -1) {

					h.imageView
							.setImageURI(Uri
									.parse("http://shaircard.appspot.com/get_vcard_image?vcardId="
											+ element.getId()));
				} else if (element.localUri != null) {
					h.imageView.setImageURI(Uri.parse(element.localUri));
				} else {
					h.imageView.setImageDrawable(null);
				}

			}

			@Override
			public Object createHolder(final View view,
					final ImagesManagerContext<?> imagesManagerContext) {
				final VcardHolder h = new VcardHolder();
				h.imageView = (LoadableImageView) view.findViewById(R.id.photo);
				h.imageView.setImagesManagerContext(imagesManagerContext);
				h.name = (TextView) view.findViewById(R.id.name);
				h.company = (TextView) view.findViewById(R.id.company);
				h.phone = (TextView) view.findViewById(R.id.phone);

				return h;
			}
		};
	}

	@Override
	protected ElementRenderer<VCard> createRenderer() {
		return RENDERER;
	}

	private ElementRenderer<VCard> RENDERER;

	@Override
	public Fetcher<VCard> createAdapter(final Context context,
			final ElementRenderer<VCard> renderer) {

		return new PageFetcher<VCard>(context, renderer, getRequestToken());
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRequestBuilder(new VcardRequestBuilder(getOwnerActivity(), eventId));

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1,
					int position, long arg3) {
				final VCard vcard = (VCard) adapter.getItemAtPosition(position);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getOwnerActivity());
				builder.setTitle(R.string.add_contact);
				builder.setMessage(R.string.add_contact_to_book);

				builder.setPositiveButton(android.R.string.yes,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AddContact.addContact(
										getOwnerActivity(),
										vcard.getFirstName() + " "
												+ vcard.getSurname(),
										vcard.getPhone(), vcard.getEmail(),
										vcard.getCompany());
								Toast.makeText(getOwnerActivity(),
										"Contact was added", Toast.LENGTH_SHORT)
										.show();
								dialog.dismiss();
							}
						});

				builder.setNegativeButton(android.R.string.no,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});

				builder.create().show();

			}
		});
	}

	public void update(String text, Float longtitude, Float latitude) {

		EventsRequestBuilder builder = new EventsRequestBuilder(
				getOwnerActivity()).setText(text);
		if (longtitude != null) {
			// TODO
		}

		setRequestBuilder(builder);

	}

}
