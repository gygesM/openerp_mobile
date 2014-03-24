/*
 * Copyright (C) 2013  stevendreamer (in github)
 * Project Location: https://github.com/stevendreamer/openerp_mobile

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * Addition: any copy of this program should keep the author name info.
 * any copy without the author info will be a pirate

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.oe.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.debortoliwines.openerp.api.FilterCollection;
import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.oe.mobile.model.Model;
import com.oe.mobile.service.Inventory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemListActivity extends Activity {

	MyApp app;
	List<Map<String, Object>> listItems;
	LinearLayout headerLayout;
	Handler handler;
	ListView list;
	MyTask mTask;

	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_item_list);

		list = (ListView) findViewById(R.id.itemlist);

		listItems = new ArrayList<Map<String, Object>>();

		dialog = ProgressDialog.show(this, "", "�������ݣ����Ե�Ƭ�� ��", true, true);
		list.setOnItemClickListener(new ItemClickListener());

		// call the asynchronized task
		mTask = new MyTask();
		mTask.execute();

	}

	public void setPageView(RowCollection rc) {

		// construct the arraylist used to show on the page
		for (Row r : rc) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("name", r.get("name_template"));
			listItem.put("quantity", r.get("qty_available"));
			listItem.put("listPrice", r.get("lst_price"));
			listItem.put("itemId", r.get("id"));
			listItems.add(listItem);
		}


		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.item_list, new String[] { "name", "quantity",
						"listPrice", "itemId" }, new int[] { R.id.name,
						R.id.quantity, R.id.listPrice, R.id.itemId });
		list.setAdapter(simpleAdapter);

		dialog.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_item_list, menu);
		return true;
	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			System.out.println("this is in the clicker");
			// get the item id of the list, and goto the item detail page
			// to show the item detail information.

			System.out.println("zzyan inside list click trigger:"
					+ " name:"
					+ ((HashMap) list.getItemAtPosition(arg2))
							.get("itemListId"));
			// parse the id of the item
			HashMap h = (HashMap) list.getItemAtPosition(arg2);
			int id = (Integer) h.get("itemListId");
			System.out.println("end of clicker");
			Intent intent = new Intent(ItemListActivity.this,
					ItemDetailActivity.class);
			intent.putExtra("productId", id);
			startActivity(intent);

		}
	}

	private class MyTask extends AsyncTask<String, Integer, RowCollection> {

		@Override
		protected void onPreExecute() {
			Log.i("ItemListPage", "onPreExecute() called");

		}

		@Override
		protected RowCollection doInBackground(String... params) {
			RowCollection result = null;
			try {
				result = Inventory.getItems();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... progresses) {

		}

		@Override
		protected void onPostExecute(RowCollection rc) {

			setPageView(rc);
		}
	}
}
