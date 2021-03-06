package com.android.dal;

import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLWorkoutReader {

	/***
	 * Reads XML file from the internal storage folder of the application.
	 * 
	 * @param fileName
	 *            Name of the XML file.
	 * @return The Exercise containing the entire workout.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static Exercise ReadFile(FileInputStream fis) throws XmlPullParserException, IOException, NumberFormatException {

		// Exercise variables
		Exercise result = new Exercise(-1);
		InputStreamReader isr = null;

		// Read stream
		isr = new InputStreamReader(fis);
		char[] inputBuffer = new char[fis.available()];
		isr.read(inputBuffer);
		String data = new String(inputBuffer);
		isr.close();
		fis.close();

		/*
		 * Converting the String data to XML format so that the parser would
		 * understand it as an XML input.
		 */
		InputStream is = new ByteArrayInputStream(data.getBytes(Consts.XML_FILE_HEADER));

		// Setup xml reader
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(is, null);

		result = parseSet(xpp);

		return result;
	}

	private static Exercise parseSet(XmlPullParser xpp) throws XmlPullParserException, IOException, NumberFormatException {
		Exercise result = new Exercise(-1);

		// Go over current Exercise tag
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Reading attributes of Exercise
				if (name.equals(Consts.XML_TAG_EXERCISE_SETS)) {
					// Set sets
					result.setSets(Integer.parseInt(readText(xpp)));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Set name
					result.setName(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Set comment
					result.setComment(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Set sound file path
					result.setSound(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_EXERCISE_ELEMENTS)) {
					// Set elements
					result.setElements(parseElements(xpp));
				}
			}
		}

		return result;
	}

	private static ArrayList<AElement> parseElements(XmlPullParser xpp) throws XmlPullParserException, IOException {
		ArrayList<AElement> result = new ArrayList<AElement>();

		// Go over elements of current Exercise
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Read elements
				if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE)) {
					// Add repetition exercise
					result.add(parseRepetitionExercise(xpp));
				}
				else if (name.equals(Consts.XML_TAG_REST)) {
					// Add rest
					result.add(parseRest(xpp));
				}
				else if (name.equals(Consts.XML_TAG_TIME_EXERCISE)) {
					// Add time exercise
					result.add(parseTimeExercise(xpp));
				}
				else if (name.equals(Consts.XML_TAG_EXERCISE)) {
					// Add Exercise
					result.add(parseSet(xpp));
				}
			}
		}

		return result;
	}

	private static Rest parseRest(XmlPullParser xpp) throws XmlPullParserException, IOException, NumberFormatException {
		Rest result = new Rest();

		// Go over attributes of current rest
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Read attributes
				if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Read name
					result.setName(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Read comment
					result.setComment(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Read sound file path
					result.setSound(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_TIME)) {
					// Read time
					result.setTime(Double.parseDouble(readText(xpp)));
				}
			}
		}

		return result;
	}

	private static RepetitionExercise parseRepetitionExercise(XmlPullParser xpp) throws XmlPullParserException, IOException {
		RepetitionExercise result = new RepetitionExercise();

		// Go over attributes of current repetition exercise
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Read attributes
				if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Read name
					result.setName(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Read comment
					result.setComment(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Read sound file path
					result.setSound(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_DATA)) {
					// Read reps, weights, endless and groups data
					while (xpp.next() != XmlPullParser.END_TAG) {
						name = xpp.getName();

						if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_REPS)) {
							// Read reps
							result.getReps().add(Integer.parseInt(readText(xpp)));
						}
						else if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_WEIGHTS)) {
							// Read weights
							result.getWeights().add(Double.parseDouble(readText(xpp)));
						}
						else if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_ENDLESS)) {
							// Read weights
							result.getEndlessSets().add(Boolean.parseBoolean(readText(xpp)));
						}
					}
				}
			}
		}

		return result;
	}

	private static TimeExercise parseTimeExercise(XmlPullParser xpp) throws XmlPullParserException, IOException, NumberFormatException {
		TimeExercise result = new TimeExercise();

		// Go over attributes of current time exercise
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Read attributes
				if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Read name
					result.setName(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Read comment
					result.setComment(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Read sound file path
					result.setSound(readText(xpp));
				}
				else if (name.equals(Consts.XML_TAG_ELEMENT_TIME)) {
					// Read time
					result.setTime(Double.parseDouble(readText(xpp)));
				}
			}
		}

		return result;
	}

	// Extracts text values.
	private static String readText(XmlPullParser xpp) throws IOException, XmlPullParserException {
		String result = "";

		if (xpp.next() == XmlPullParser.TEXT) {
			result = xpp.getText();
			xpp.nextTag();
		}

		return result;
	}

	private XMLWorkoutReader() {
	}
}
