/*
 * Copyright (c) 2017. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.adapters.HomeCardsAdapter
import jahirfiquitiva.libs.blueprint.extensions.openLink
import jahirfiquitiva.libs.blueprint.fragments.presenters.ItemsFragmentPresenter
import jahirfiquitiva.libs.blueprint.models.HomeCard
import jahirfiquitiva.libs.blueprint.models.lists.ListsHolder
import jahirfiquitiva.libs.blueprint.tasks.BasicTaskLoader
import jahirfiquitiva.libs.blueprint.tasks.HomeCardsLoader
import jahirfiquitiva.libs.blueprint.ui.views.EmptyViewRecyclerView
import jahirfiquitiva.libs.blueprint.utils.DEFAULT_HOME_POSITION

@SuppressLint("ValidFragment")
class HomeFragment(val nContext:Context):Fragment(), ItemsFragmentPresenter<ArrayList<HomeCard>> {

    private lateinit var rv:EmptyViewRecyclerView
    private lateinit var homeAdapter:HomeCardsAdapter

    override fun getContext():Context {
        return nContext
    }

    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater?.inflate(R.layout.section_home, container, false) as View
        initUI(content)
        return content
    }

    override fun onViewCreated(view:View?, savedInstanceState:Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ListsHolder.lists.appsLinksList.isEmpty()) executeTask(context)
        else {
            try {
                homeAdapter.clearAndAddAll(ListsHolder.lists.appsLinksList.list)
                rv.updateState(EmptyViewRecyclerView.STATE_NORMAL)
            } catch (ignored:Exception) {
            }
        }
    }

    override fun initUI(content:View) {
        rv = content.findViewById(R.id.home_rv)
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text)
        rv.updateState(EmptyViewRecyclerView.STATE_LOADING)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        /*
        val deco = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        deco.setDrawable(ColorDrawable(context.getDividerColor()))
        rv.addItemDecoration(deco)
        */
        homeAdapter = HomeCardsAdapter(context, { onItemClicked(it) })
        rv.setHasFixedSize(true)
        rv.adapter = homeAdapter
    }

    override fun onItemClicked(item:Any) {
        if (item is HomeCard) {
            if (item.intent != null) context.startActivity(item.intent)
            else context.openLink(item.url)
        }
    }

    override fun getLoaderId():Int = DEFAULT_HOME_POSITION

    override fun buildLoader():Loader<ArrayList<HomeCard>> =
            HomeCardsLoader(context, object:BasicTaskLoader.TaskListener {
                override fun onTaskStarted() {
                    rv.updateState(
                            EmptyViewRecyclerView.STATE_LOADING)
                }
            })

    override fun onDataLoadFinished(data:ArrayList<HomeCard>) {
        val adapter = rv.adapter
        if (adapter is HomeCardsAdapter) {
            ListsHolder.lists.appsLinksList.createList(data)
            adapter.clearAndAddAll(data)
            rv.updateState(EmptyViewRecyclerView.STATE_NORMAL)
        }
    }
}