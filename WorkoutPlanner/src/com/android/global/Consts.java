package com.android.global;

public class Consts {

	// Tags
	public static final String SELECT_WORKOUT_TAG = "SelectWorkout";
	public static final String PREFS_FILE_TAG = "PreferenceFiles";
	public static final String REST_OFFSET_TAG = "RestOffset";

	// XML Tags
	public static final String XML_TAG_EXERCISE = "exercise";
	public static final String XML_TAG_EXERCISE_SETS = "sets";
	public static final String XML_TAG_EXERCISE_ENDLESS = "endless";
	public static final String XML_TAG_ELEMENT_COMMENT = "comment";
	public static final String XML_TAG_ELEMENT_NAME = "name";
	public static final String XML_TAG_ELEMENT_SOUND = "sound";
	public static final String XML_TAG_EXERCISE_ELEMENTS = "elements";
	public static final String XML_TAG_REPETITION_EXERCISE_DATA = "repExData";
	public static final String XML_TAG_REPETITION_EXERCISE_REPS = "repExReps";
	public static final String XML_TAG_REPETITION_EXERCISE_WEIGHTS = "repExWeights";
	public static final String XML_TAG_REST = "rest";
	public static final String XML_TAG_ELEMENT_TIME = "time";
	public static final String XML_TAG_REPETITION_EXERCISE = "repetitionExercise";
	public static final String XML_TAG_TIME_EXERCISE = "timeExercise";
	public static final String XML_FILE_HEADER = "UTF-8";
	
	// Others
	public static final String FILE_EXTENSION = ".xml";
	public static final String WORKOUT_NAME_KEY = "WorkoutName";
	public static final String SET_SETS_METHOD_NAME = "setSets";
	public static final String SET_TIME_METHOD_NAME = "setTime";
	public static final String GET_REPS_MEHOD_NAME = "getReps";
	public static final String GET_WEIGHTS_METHOD_NAME = "getWeights";
	public static final String CURRENT_EXERCISE = "CurrentExercise";
	public static final int DEFAULT_SET_VALUE = 1;
	
	// Enums
	public enum elementTypes {
		REPETITIONS_INDEX,
		REST_INDEX,
		TIME_INDEX,
	}
	
	public enum resultActivities {
		ELEMENTS_LIST,
		PREVIEW_ACTIVITY,
	}
	
	// Toast Texts
	public static final String SAVE_SUCCESS = "File saved successfully";
}
