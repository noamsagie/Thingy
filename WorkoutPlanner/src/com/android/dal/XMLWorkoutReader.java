package com.android.dal;

import com.android.element.AElement;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.Set;
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
	 * @return The Set containing the entire workout.
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static Set ReadFile(FileInputStream fis)
			throws XmlPullParserException, IOException, NumberFormatException {

		// Set variables
		Set result = new Set(-1);
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
		InputStream is = new ByteArrayInputStream(
				data.getBytes(Consts.XML_FILE_HEADER));

		// Setup xml reader
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(is, null);
		
		result = parseSet(xpp);

		return result;
	}

	private static Set parseSet(XmlPullParser xpp)
			throws XmlPullParserException, IOException, NumberFormatException {
		Set result = new Set(-1);

		// Go over current set tag
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Reading attributes of set
				if (name.equals(Consts.XML_TAG_SET_REPETITIONS)) {
					// Set repetitions
					result.setRepetitions(Integer.parseInt(readText(xpp)));
				} else if (name.equals(Consts.XML_TAG_SET_ENDLESS)) {
					// Set endless
					result.setEndless(Boolean.parseBoolean(readText(xpp)));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Set name
					result.setName(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Set comment
					result.setComment(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Set sound file path
					result.setSound(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_SET_ELEMENTS)) {
					// Set elements
					result.setElements(parseElements(xpp));
				}
			}
		}

		return result;
	}

	private static ArrayList<AElement> parseElements(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		ArrayList<AElement> result = new ArrayList<AElement>();

		// Go over elements of current set
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Reading elements
				if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE)) {
					// Add repetition exercise
					result.add(parseRepetitionExercise(xpp));
				} else if (name.equals(Consts.XML_TAG_REST)) {
					// Add rest
					result.add(parseRest(xpp));
				} else if (name.equals(Consts.XML_TAG_TIME_EXERCISE)) {
					// Add time exercise
					result.add(parseTimeExercise(xpp));
				} else if (name.equals(Consts.XML_TAG_SET)) {
					// Add set
					result.add(parseSet(xpp));
				}
			}
		}

		return result;
	}

	private static Rest parseRest(XmlPullParser xpp)
			throws XmlPullParserException, IOException, NumberFormatException {
		Rest result = new Rest();

		// Go over attributes of current rest
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Reading attributes
				if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Set name
					result.setName(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Set comment
					result.setComment(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Set sound file path
					result.setSound(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_TIME)) {
					// Set time
					result.setTime(Double.parseDouble(readText(xpp)));
				}
			}
		}

		return result;
	}

	private static RepetitionExercise parseRepetitionExercise(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		RepetitionExercise result = new RepetitionExercise();

		// Go over attributes of current repetition exercise
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Reading attributes
				if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Set name
					result.setName(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Set comment
					result.setComment(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Set sound file path
					result.setSound(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_DATA)) {
					// Set reps and weights data
					while (xpp.next() != XmlPullParser.END_TAG) {
						name = xpp.getName();
						
						if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_REPS)) {
							// Set reps
							result.getReps().add(Integer.parseInt(readText(xpp)));
						} else if (name.equals(Consts.XML_TAG_REPETITION_EXERCISE_WEIGHTS)) {
							// Set weights
							result.getWeights().add(Double.parseDouble(readText(xpp)));
						}
					}
				}
			}
		}

		return result;
	}

	private static TimeExercise parseTimeExercise(XmlPullParser xpp)
			throws XmlPullParserException, IOException, NumberFormatException {
		TimeExercise result = new TimeExercise();

		// Go over attributes of current time exercise
		while (xpp.next() != XmlPullParser.END_TAG) {
			String name = xpp.getName();

			// Validate name
			if (name != null) {
				// Reading attributes
				if (name.equals(Consts.XML_TAG_ELEMENT_NAME)) {
					// Set name
					result.setName(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_COMMENT)) {
					// Set comment
					result.setComment(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_SOUND)) {
					// Set sound file path
					result.setSound(readText(xpp));
				} else if (name.equals(Consts.XML_TAG_ELEMENT_TIME)) {
					// Set time
					result.setTime(Double.parseDouble(readText(xpp)));
				}
			}
		}

		return result;
	}

	// Extracts text values.
	private static String readText(XmlPullParser xpp) throws IOException,
			XmlPullParserException {
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
