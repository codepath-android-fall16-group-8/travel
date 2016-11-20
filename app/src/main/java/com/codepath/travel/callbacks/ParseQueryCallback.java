package com.codepath.travel.callbacks;

import com.parse.ParseException;

/**
 * Created by rpraveen on 11/19/16.
 */

public interface ParseQueryCallback<T> {
  void onQuerySuccess(T data);
  void onQueryError(ParseException e);
  //void onQueryListSuccess(List<T> data);
}
