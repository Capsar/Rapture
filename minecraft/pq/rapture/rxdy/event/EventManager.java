package pq.rapture.rxdy.event;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.annotations.Filters;
import pq.rapture.rxdy.event.filter.Filter;

/**
 * Created by Haze on 4/15/2015.
 */
public class EventManager {

	private static EventManager _instance;
	private Map<Class<?>[], Set<CallbackData>> data;
	private Map<Class<?>, Set<CallbackData>> registeredClasses;

	public EventManager() {
		data = new HashMap<Class<?>[], Set<CallbackData>>();
		registeredClasses = new HashMap<Class<?>, Set<CallbackData>>();
	}

	public static EventManager getInstance() {
		if (_instance == null) {
			_instance = new EventManager();
		}
		return _instance;
	}

	private static Class<?>[] getClassesFromArray(Event[] eVents) {
		Class<?>[] list = new Class[eVents.length];
		int helper = 0;
		for (Event e : eVents) {
			list[helper] = eVents[helper].getClass();
			helper++;
		}
		return list;
	}

	public synchronized void unregisterAll(Object o) {
		Map<Class<?>[], Set<CallbackData>> copyDataSet = new HashMap<Class<?>[], Set<CallbackData>>();
		copyDataSet.putAll(data);
		for (Map.Entry<Class<?>[], Set<CallbackData>> e : copyDataSet.entrySet()) {
			for (CallbackData _datas : e.getValue()) {
				if (_datas.object == o) {
					data.remove(e.getKey());
				}
			}
		}
	}

	public synchronized void register(Object o, Class<? extends Event> targetEventKlass) {
		if (o == null)
			return;
		try {
			Class<?> klass = o.getClass();
			for (Method m : klass.getDeclaredMethods()) {
				if (m.isAnnotationPresent(ETarget.class)) {
					Parameter[] params = m.getParameters();
					if (params.length > 0) {
						boolean areParamsEvents = true;
						for (Parameter p : params) {
							areParamsEvents &= Event.class.isAssignableFrom(p.getType());
						}
						if (areParamsEvents) {
							Class<?>[] eventClasses = new Class<?>[params.length];
							for (int i = 0; i < params.length; i++) {
								eventClasses[i] = params[i].getType();
							}

							Set<Filter> filters = null;
							boolean superPass = true;
							for (Class<?> clazz : eventClasses) {
								superPass &= targetEventKlass.isAssignableFrom(clazz);
								if (targetEventKlass.isAssignableFrom(clazz)) {
									if (m.isAnnotationPresent(Filters.class)) {
										filters = getFiltersFor(m.getAnnotation(Filters.class));
									}

								}
							}
							if (superPass) {
								CallbackData data = new CallbackData(o, m, eventClasses, filters, true);
								Set<CallbackData> datas = this.data.get(eventClasses);
								if (datas == null) {
									datas = new HashSet<CallbackData>();
									this.data.put(eventClasses, datas);
								}
								datas.add(data);
								register0(data);
							}
						} else {
							boolean superPass = true;
							Set<Filter> filters = null;
							Class<?>[] eventArray = new Class<?>[m.getAnnotation(ETarget.class).eventClasses().length];
							int helper = 0;
							for (Class<? extends Event> clazz : m.getAnnotation(ETarget.class).eventClasses()) {
								superPass &= targetEventKlass.isAssignableFrom(clazz);
								eventArray[helper] = clazz;
								if (m.isAnnotationPresent(Filters.class)) {
									filters = getFiltersFor(m.getAnnotation(Filters.class));
								}
								helper++;
							}
							if (superPass) {
								CallbackData data = new CallbackData(o, m, eventArray, filters, true);
								Set<CallbackData> datas = this.data.get(eventArray);
								if (datas == null) {
									datas = new HashSet<CallbackData>();
									this.data.put(eventArray, datas);
								}
								datas.add(data);
								register0(data);
							}
						}
					} else {
						boolean superPass = true;
						Set<Filter> filters = null;
						Class<?>[] eventArray = new Class<?>[m.getAnnotation(ETarget.class).eventClasses().length];
						int helper = 0;
						for (Class<? extends Event> clazz : m.getAnnotation(ETarget.class).eventClasses()) {
							superPass &= targetEventKlass.isAssignableFrom(clazz);
							eventArray[helper] = clazz;
							if (m.isAnnotationPresent(Filters.class)) {
								filters = getFiltersFor(m.getAnnotation(Filters.class));
							}
							helper++;
						}
						if (superPass) {
							CallbackData data = new CallbackData(o, m, eventArray, filters, true);
							Set<CallbackData> datas = this.data.get(eventArray);
							if (datas == null) {
								datas = new HashSet<CallbackData>();
								this.data.put(eventArray, datas);
							}
							datas.add(data);
							register0(data);
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public synchronized void registerAll(Object o) {
		register(o, Event.class);
	}

	private synchronized void register0(CallbackData data) {
		Class<?> klass = data.object.getClass();
		Set<CallbackData> datas = this.registeredClasses.get(klass);
		if (datas == null) {
			datas = new HashSet<CallbackData>();
			this.registeredClasses.put(klass, datas);
		}
		datas.add(data);
	}

	private Set<Filter> getFiltersFor(Filters anno) {
		Class<? extends Filter>[] filters = anno.filters();
		if (filters.length == 0)
			return null;
		Set<Filter> set = new HashSet<Filter>(filters.length);
		for (Class<? extends Filter> filterKlass : filters) {
			try {
				Filter filter = filterKlass.newInstance();
				set.add(filter);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	public synchronized void fire(Event[] events) {
		if (events.length != 0) {
			try {
				Map<Class<?>[], Set<CallbackData>> copyDataSet = new HashMap<Class<?>[], Set<CallbackData>>();
				copyDataSet.putAll(data);
				for (Map.Entry<Class<?>[], Set<CallbackData>> entr : copyDataSet.entrySet()) {
					for (CallbackData _data : entr.getValue()) {
						if (_data.callbackMethod.isAnnotationPresent(EventAllowance.class)) {
							switch (_data.callbackMethod.getAnnotation(EventAllowance.class).allowance()) {
							case ACCEPT_ONLY_ALL:
								if (Arrays.deepEquals(_data.eventKlass, getClassesFromArray(events))) {
									_data.invoke(events);
								}
								break;
							case ALLOW_ANY:
								for (Class<?> clazz : _data.eventKlass) {
									for (Event e : events) {
										if (e.getClass().isAssignableFrom(clazz)) {
											_data.invoke(events);
										}
									}
								}
							}
						} else {
							if (Arrays.deepEquals(_data.eventKlass, getClassesFromArray(events))) {
								_data.invoke(events);
							}
						}
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void fire(Event e) {
		if (e == null)
			return;
		fire(new Event[] { e });
	}

	static class CallbackData {
		private final Object object;
		private final Method callbackMethod;
		private final Set<Filter> filters;
		private final boolean hasParams;
		private final boolean isArrayOfEvents;
		private Class<?>[] eventKlass;

		public CallbackData(Object object, Method callbackMethod, Class<?>[] eventKlass, Set<Filter> filters, boolean hasParams) {
			this.eventKlass = new Class<?>[eventKlass.length];
			this.object = object;
			this.callbackMethod = callbackMethod;
			this.eventKlass = eventKlass;
			this.filters = filters == null ? new HashSet<Filter>() : filters;
			this.hasParams = hasParams;
			this.isArrayOfEvents = true;
			callbackMethod.setAccessible(true);
		}

		public boolean hasFilters() {
			return filters != null && filters.size() != 0;
		}

		public void invoke(Event[] events) throws Throwable {

			/*
			 * lets do filters! if filters is not empty, evaluate. if ANY failed
			 * to filter, return.
			 */
			boolean filterPass = true;
			if (!filters.isEmpty()) {
				Filters annotationFilter = callbackMethod.getAnnotation(Filters.class);
				for (Filter f : filters) {
					if (annotationFilter.targets() != null) {
						boolean classPass = true;
						for (Class<? extends Event> clazz : annotationFilter.targets()) {
							for (Event e : events) {
								classPass &= e.getClass().isAssignableFrom(clazz);
							}
						}
						if (classPass) {
							for (Event e : events) {
								filterPass &= f.isAcceptable(e);
							}
						}
					} else {
						for (Event e : events) {
							filterPass &= f.isAcceptable(e);
						}
					}
				}
			}
			if (filterPass) {
				if (events.length == 1) {
					if (callbackMethod.getParameterCount() == 0) {
						callbackMethod.invoke(object);
					} else if (callbackMethod.getParameterCount() == 1) {
						callbackMethod.invoke(object, events);
					} else if (callbackMethod.getParameterCount() > 1) {
						Event[] neededEventArray = new Event[callbackMethod.getParameterCount()];
						int place = -1, helper = 0;
						for (Parameter p : callbackMethod.getParameters()) {
							if (events[0].getClass().isAssignableFrom(p.getType())) {
								place = helper;
								break;
							}
							helper++;
						}
						for (int i = 0; i < callbackMethod.getParameterCount(); i++) {
							if (i == place) {
								neededEventArray[i] = events[0];
							} else {
								neededEventArray[i] = null;
							}
						}
						callbackMethod.invoke(object, neededEventArray);
					}
				} else {

					Event[] neededEventListv = new Event[callbackMethod.getParameterCount()];
					Map<Integer, Event> placeToEvent = new HashMap<>();
					int help = 0;
					for (Parameter p : callbackMethod.getParameters()) {
						for (Event ev : events) {
							if (ev.getClass().isAssignableFrom(p.getType())) {
								placeToEvent.put(help, ev);
							}
						}
						help++;
					}
					boolean foundPlace;
					for (int i = 0; i < callbackMethod.getParameterCount(); i++) {
						foundPlace = false;
						for (int place : placeToEvent.keySet()) {
							if (place == i) {
								neededEventListv[i] = placeToEvent.get(place);
								foundPlace = true;
							}
						}
						if (!foundPlace)
							neededEventListv[i] = null;
					}
					callbackMethod.invoke(object, neededEventListv);
				}
			}
		}
	}

}
