package com.andframe.annotation.interpreter;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.andframe.annotation.pager.BindLayout;
import com.andframe.annotation.view.BindCheckedChange;
import com.andframe.annotation.view.BindCheckedChangeGroup;
import com.andframe.annotation.view.BindClick;
import com.andframe.annotation.view.BindItemClick;
import com.andframe.annotation.view.BindItemLongClick;
import com.andframe.annotation.view.BindLongClick;
import com.andframe.annotation.view.BindTouch;
import com.andframe.annotation.view.BindView;
import com.andframe.annotation.view.BindViewCreated;
import com.andframe.annotation.view.BindViewModule;
import com.andframe.api.view.Viewer;
import com.andframe.application.AfApp;
import com.andframe.exception.AfExceptionHandler;
import com.andframe.impl.wrapper.ViewWrapper;
import com.andframe.module.AfFrameSelector;
import com.andframe.module.AfSelectorBottombar;
import com.andframe.module.AfSelectorBottombarImpl;
import com.andframe.module.AfSelectorTitlebar;
import com.andframe.module.AfSelectorTitlebarImpl;
import com.andframe.module.AfViewModuler;
import com.andframe.util.java.AfReflecter;
import com.andframe.widget.AfContactsRefreshView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.andframe.annotation.interpreter.SmartInvoke.paramAllot;


/**
 * 控件绑定器
 *
 * @author 树朾
 */
@SuppressWarnings("unused")
public class ViewBinder {

    protected static String TAG(Object obj, String tag) {
        if (obj == null) {
            return "ViewBinder." + tag;
        }
        return "LayoutBinder(" + obj.getClass().getName() + ")." + tag;
    }

    public static void doBind(Viewer root) {
        doBind(root, root);
    }

    public static void doBind(Object handler, Viewer root) {
        bindClick(handler, root);
        bindTouch(handler, root);
        bindLongClick(handler, root);
        bindItemClick(handler, root);
        bindItemLongClick(handler, root);
        bindCheckedChange(handler, root);
        bindCheckedChangeGroup(handler, root);
        bindView(handler, root);
        bindViewModule(handler, root);
        bindViewCreated(handler);
    }

    private static Class<?> getStopType(Object handler) {
        if (handler instanceof ViewWrapper) {
            return ViewWrapper.class;
        }
        if (handler instanceof Activity) {
            return Activity.class;
        }
        if (handler instanceof Fragment) {
            return Fragment.class;
        }
        return Object.class;
    }

    private static void bindTouch(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindTouch.class)) {
            try {
                BindTouch bind = method.getAnnotation(BindTouch.class);
                for (int id : bind.value()) {
                    View view = root.findViewById(id);
                    view.setOnTouchListener(new EventListener(handler).touch(method));
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindClick.") + method.getName());
            }
        }
    }

    private static void bindClick(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindClick.class)) {
            try {
                BindClick bind = method.getAnnotation(BindClick.class);
                for (int id : bind.value()) {
                    View view = root.findViewById(id);
                    view.setOnClickListener(new EventListener(handler).click(method, bind.intervalTime()));
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindClick.") + method.getName());
            }
        }
    }

    private static void bindLongClick(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindLongClick.class)) {
            try {
                BindLongClick bind = method.getAnnotation(BindLongClick.class);
                for (int id : bind.value()) {
                    View view = root.findViewById(id);
                    view.setOnLongClickListener(new EventListener(handler).longClick(method));
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindLongClick.") + method.getName());
            }
        }
    }

    private static void bindItemClick(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindItemClick.class)) {
            try {
                BindItemClick bind = method.getAnnotation(BindItemClick.class);
                if (bind.value().length == 0) {
                    AfApp.get().newViewQuery(root).$(AdapterView.class).itemClicked(new EventListener(handler).itemClick(method, bind.intervalTime()));
                } else for (int id : bind.value()) {
                    AdapterView<?> view = root.findViewByID(id);
                    if (view != null) {
                        view.setOnItemClickListener(new EventListener(handler).itemClick(method, bind.intervalTime()));
                    }
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindLongClick.") + method.getName());
            }
        }
    }

    private static void bindItemLongClick(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindItemLongClick.class)) {
            try {
                BindItemLongClick bind = method.getAnnotation(BindItemLongClick.class);
                if (bind.value().length == 0) {
                    AfApp.get().newViewQuery(root).$(AdapterView.class).itemLongClicked((new EventListener(handler).itemLongClick(method)));
                } else for (int id : bind.value()) {
                    AdapterView<?> view = root.findViewByID(id);
                    if (view != null) {
                        view.setOnItemLongClickListener(new EventListener(handler).itemLongClick(method));
                    }
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindLongClick.") + method.getName());
            }
        }
    }

    private static void bindCheckedChange(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindCheckedChange.class)) {
            try {
                BindCheckedChange bind = method.getAnnotation(BindCheckedChange.class);
                for (int id : bind.value()) {
                    CompoundButton view = root.findViewByID(id);
                    if (view != null) {
                        view.setOnCheckedChangeListener(new EventListener(handler).checkedChange(method));
                    }
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindLongClick.") + method.getName());
            }
        }
    }

    private static void bindCheckedChangeGroup(Object handler, Viewer root) {
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindCheckedChangeGroup.class)) {
            try {
                BindCheckedChangeGroup bind = method.getAnnotation(BindCheckedChangeGroup.class);
                for (int id : bind.value()) {
                    RadioGroup view = root.findViewByID(id);
                    if (view != null) {
                        view.setOnCheckedChangeListener(new EventListener(handler).checkedChangeGroup(method));
                    }
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindLongClick.") + method.getName());
            }
        }
    }

    private static void bindView(Object handler, Viewer root) {
        for (Field field : AfReflecter.getFieldAnnotation(handler.getClass(), getStopType(handler), BindView.class)) {
            try {
                BindView bind = field.getAnnotation(BindView.class);
                List<View> list = new ArrayList<>();
                for (int id : bind.value()) {
                    View view = null;
                    if (id > 0) {
                        view = root.findViewById(id);
                    } else if (bind.value().length > 1 || (!field.getType().isArray() && !List.class.equals(field.getType()))) {
                        View[] receiveViews = findViewByType(root.getView(), field.getType(), 1);
                        if (receiveViews.length > 0) {
                            view = receiveViews[0];
                        }
                    } else {
                        field.setAccessible(true);
                        Object original = field.get(handler);
                        View[] receiveViews;
                        if (original != null && field.getType().isArray()) {
                            Object[] objects = (Object[]) original;
                            receiveViews = findViewByType(root.getView(), field.getType(), objects.length);
                        } else {
                            receiveViews = findViewByType(root.getView(), field.getType(), 0);
                        }
                        list.addAll(Arrays.asList(receiveViews));
                    }
                    if (view != null) {
                        if (bind.click() && handler instanceof OnClickListener) {
                            view.setOnClickListener((OnClickListener) handler);
                        }
                        list.add(view);
                    }
                }
                if (list.size() > 0) {
                    field.setAccessible(true);
                    if (field.getType().isArray()) {
                        Class<?> componentType = field.getType().getComponentType();
                        Object[] array = list.toArray((Object[]) Array.newInstance(componentType, list.size()));
                        field.set(handler, array);
                    } else if (List.class.equals(field.getType())) {
                        field.set(handler, list);
                    } else if (list.get(0) != null) {
                        field.set(handler, list.get(0));
                    }
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindView.") + field.getName());
            }
        }
    }

    private static View[] findViewByType(View rootview, Class<?> type, int count) {
        if (type.isArray()) {
            type = type.getComponentType();
        }
        count = count <= 0 ? Integer.MAX_VALUE : count;

        Queue<View> views = new LinkedBlockingQueue<>(Collections.singletonList(rootview));
        List<View> list = new ArrayList<>(count == Integer.MAX_VALUE ? 0 : count);
        do {
            View cview = views.poll();
            if (cview != null && type.isAssignableFrom(cview.getClass())) {
                list.add(cview);
            } else {
                if (cview instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) cview;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        views.add(group.getChildAt(j));
                    }
                }
            }
        } while (!views.isEmpty() && list.size() < count);

        return list.toArray(new View[list.size()]);
    }

    @SuppressWarnings("unchecked")
    private static void bindViewModule(Object handler,@NonNull Viewer root) {
        for (Field field : AfReflecter.getFieldAnnotation(handler.getClass(), getStopType(handler), BindViewModule.class)) {
            try {
                Class<?> clazz = field.getType();
                BindViewModule bind = field.getAnnotation(BindViewModule.class);
                List<Object> list = new ArrayList<>();
                for (int id : bind.value()) {
                    Object value = null;
                    if (clazz.equals(AfSelectorTitlebar.class)) {
                        value = new AfSelectorTitlebarImpl(root);
                    } else if (clazz.equals(AfSelectorBottombar.class)) {
                        value = new AfSelectorBottombarImpl(root);
                    } else if (clazz.equals(AfFrameSelector.class)) {
                        value = new AfFrameSelector(root, id);
                    } else if (clazz.equals(AfContactsRefreshView.class)) {
                        value = new AfContactsRefreshView(root, bind.value()[0]);
                    } else if ((field.getType().isAnnotationPresent(BindLayout.class) || id > 0)) {

                        Class<?> type = field.getType();
                        if (type.isArray()) {
                            type = field.getType().getComponentType();
                        } else if (List.class.isAssignableFrom(type)) {
                            Type generic = field.getGenericType();
                            ParameterizedType parameterized = (ParameterizedType) generic;
                            type = (Class<?>) parameterized.getActualTypeArguments()[0];
                        }

                        if (id <= 0) {
                            if (type.isAnnotationPresent(BindLayout.class)) {
                                id = field.getType().getAnnotation(BindLayout.class).value();
                            }
                        }
                        if (id <= 0) {
                            AfExceptionHandler.handle("ViewModuler 必须指定BindLayout",TAG(handler, "doBindViewModule.") + field.getName());
                        } else if (AfViewModuler.class.isAssignableFrom(type)) {
                            value = AfViewModuler.init(handler, (Class<? extends AfViewModuler>) type, root, id);
                        } else {
                            AfExceptionHandler.handle("BindViewModule的类型必须继承AfViewModuler",TAG(handler, "doBindViewModule.") + field.getName());
                        }
                    }
                    if (value != null) {
                        list.add(value);
                    }
                }

                if (list.size() > 0) {
                    field.setAccessible(true);
                    if (field.getType().isArray()) {
                        Class<?> componentType = field.getType().getComponentType();
                        Object[] array = list.toArray((Object[]) Array.newInstance(componentType, list.size()));
                        field.set(handler, array);
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        field.set(handler, list);
                    } else if (list.get(0) != null) {
                        field.set(handler, list.get(0));
                    }
                }
            } catch (Throwable e) {
                AfExceptionHandler.handle(e, TAG(handler, "doBindViewModule.") + field.getName());
            }
        }
    }

    public static void bindViewCreated(Object handler) {
        List<SimpleEntry> methods = new ArrayList<>();
        for (Method method : AfReflecter.getMethodAnnotation(handler.getClass(), getStopType(handler), BindViewCreated.class)) {
            BindViewCreated annotation = method.getAnnotation(BindViewCreated.class);
            methods.add(new SimpleEntry(method, annotation));
        }
        Collections.sort(methods, (lhs, rhs) -> lhs.getValue().value() - rhs.getValue().value());
        for (SimpleEntry entry : methods) {
            try {
                invokeMethod(handler, entry.getKey());
            } catch (Throwable e) {
                e.printStackTrace();
                if (!entry.getValue().exception()) {
                    throw new RuntimeException("调用视图初始化失败", e);
                }
                AfExceptionHandler.handle(e, TAG(handler, "doBindView.") + entry.getKey().getName());
            }
        }
    }

    private static Object invokeMethod(Object handler, Method method, Object... params) throws Exception {
        if (handler != null && method != null) {
            method.setAccessible(true);
            return method.invoke(handler, params);
        }
        return null;
    }

    public static class SimpleEntry {

        private final Method key;
        private BindViewCreated value;

        public SimpleEntry(Method theKey, BindViewCreated theValue) {
            key = theKey;
            value = theValue;
        }

        public Method getKey() {
            return key;
        }

        public BindViewCreated getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }


    public static class EventListener implements OnClickListener,
            View.OnLongClickListener,
            AdapterView.OnItemClickListener,
            AdapterView.OnItemLongClickListener,
            CompoundButton.OnCheckedChangeListener,
            RadioGroup.OnCheckedChangeListener, View.OnTouchListener {

        private Object handler;

        private int clickIntervalTime = 1000;
        private long lastClickTime = 0;

        private Method clickMethod;
        private Method touchMethod;
        private Method longClickMethod;
        private Method itemClickMethod;
        private Method itemLongClickMehtod;
        private Method checkedChangedMehtod;
        private Method checkedChangedMehtodGroup;

        public EventListener(Object handler) {
            this.handler = handler;
        }

        public OnClickListener click(Method method) {
            clickMethod = method;
            return this;
        }

        public OnClickListener click(Method method, int intervalTime) {
            clickMethod = method;
            clickIntervalTime = intervalTime;
            return this;
        }

        public View.OnTouchListener touch(Method method) {
            touchMethod = method;
            return this;
        }

        public View.OnLongClickListener longClick(Method method) {
            this.longClickMethod = method;
            return this;
        }

        public AdapterView.OnItemClickListener itemClick(Method method) {
            this.itemClickMethod = method;
            return this;
        }

        public AdapterView.OnItemClickListener itemClick(Method method, int intervalTime) {
            this.itemClickMethod = method;
            this.clickIntervalTime = intervalTime;
            return this;
        }

        public AdapterView.OnItemLongClickListener itemLongClick(Method method) {
            this.itemLongClickMehtod = method;
            return this;
        }

        public CompoundButton.OnCheckedChangeListener checkedChange(Method method) {
            this.checkedChangedMehtod = method;
            return this;
        }

        public RadioGroup.OnCheckedChangeListener checkedChangeGroup(Method method) {
            this.checkedChangedMehtodGroup = method;
            return this;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return Boolean.valueOf(true).equals(invokeMethod(handler, touchMethod, v, event));
        }

        public void onClick(View v) {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - lastClickTime > clickIntervalTime) {
                lastClickTime = timeMillis;
                invokeMethod(handler, clickMethod, v);
            }
        }

        public boolean onLongClick(View v) {
            return Boolean.valueOf(true).equals(invokeMethod(handler, longClickMethod, v));
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - lastClickTime > clickIntervalTime) {
                lastClickTime = timeMillis;
                invokeMethod(handler, itemClickMethod, parent, view, position, id);
            }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return Boolean.valueOf(true).equals(invokeMethod(handler, itemLongClickMehtod, parent, view, position, id));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            invokeMethod(handler, checkedChangedMehtod, buttonView, isChecked);
        }

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            invokeMethod(handler, checkedChangedMehtodGroup, radioGroup, i);
        }

        private Object invokeMethod(Object handler, Method method, Object... params) {
            if (handler != null && method != null) {
                try {
                    method.setAccessible(true);
                    return method.invoke(handler, paramAllot(method, params));
                } catch (Throwable e) {
                    e.printStackTrace();
                    AfExceptionHandler.handle(e, "EventListener.invokeMethod");
                }
            }
            return null;
        }

    }


}
