package com.android.dal;

import android.util.Xml;
import com.android.element.AElement;
import com.android.element.Exercise;
import com.android.element.RepetitionExercise;
import com.android.element.Rest;
import com.android.element.TimeExercise;
import com.android.global.Consts;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class XMLWorkoutWriter {

	/***
	 * Writes workout into XML file and saves it in the assets folder of the
	 * application.
	 * 
	 * @param fatherExercise
	 *            The Exercise containing the entire workout.
	 * @return True if the writing and saving of the file completed
	 *         successfully. False if not.
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 * @throws XmlPullParserException
	 */
	public static boolean WriteFile(Exercise fatherSet, FileOutputStream fos) throws IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {

		// Exercise variables
		boolean result = false;
		XmlSerializer xmlSerializer = Xml.newSerializer();

		xmlSerializer.setOutput(fos, Consts.XML_FILE_HEADER);

		// Start document
		xmlSerializer.startDocument(Consts.XML_FILE_HEADER, true);

		// Convert all father Exercise data into xml
		parseSet(xmlSerializer, fatherSet);

		// End document and close stream
		xmlSerializer.endDocument();
		xmlSerializer.flush();
		fos.close();
		result = true;

		return result;
	}

	private static void parseSet(XmlSerializer xmlSerializer, Exercise set) throws XmlPullParserException, IOException, NumberFormatException {

		// Start Exercise tag
		xmlSerializer.startTag(null, Consts.XML_TAG_EXERCISE);

		// Start name tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Set name value
		xmlSerializer.text(set.getName());

		// End name tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Start comment tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Set comment value
		xmlSerializer.text(set.getComment());

		// End comment tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Start sound tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Set sound value
		xmlSerializer.text(set.getSound());

		// End sound tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Start repetitions tag
		xmlSerializer.startTag(null, Consts.XML_TAG_EXERCISE_SETS);

		// Set sets value
		xmlSerializer.text(String.valueOf(set.getSets()));

		// End repetitions tag
		xmlSerializer.endTag(null, Consts.XML_TAG_EXERCISE_SETS);

		// Start endless tag
		xmlSerializer.startTag(null, Consts.XML_TAG_EXERCISE_ENDLESS);

		// Set endless value
		xmlSerializer.text(String.valueOf(set.getEndless()));

		// End endless tag
		xmlSerializer.endTag(null, Consts.XML_TAG_EXERCISE_ENDLESS);

		// Start elements tag
		xmlSerializer.startTag(null, Consts.XML_TAG_EXERCISE_ELEMENTS);

		// Set elements
		parseElements(xmlSerializer, set.getElements());

		// End elements tag
		xmlSerializer.endTag(null, Consts.XML_TAG_EXERCISE_ELEMENTS);

		// End Set tag
		xmlSerializer.endTag(null, Consts.XML_TAG_EXERCISE);

	}

	private static void parseElements(XmlSerializer xmlSerializer, ArrayList<AElement> elements) throws XmlPullParserException, IOException {

		// Writing elements
		for (AElement element : elements) {
			// Call the fitting method to parse each element, according to its
			// type
			if (element instanceof RepetitionExercise) {
				// Parse RepetitionExercise
				parseRepetitionExercise(xmlSerializer, (RepetitionExercise) element);
			}
			else if (element instanceof Rest) {
				// Parse Rest
				parseRest(xmlSerializer, (Rest) element);
			}
			else if (element instanceof TimeExercise) {
				// Parse TimeExercise
				parseTimeExercise(xmlSerializer, (TimeExercise) element);
			}
			else if (element instanceof Exercise) {
				// Parse Set
				parseSet(xmlSerializer, (Exercise) element);
			}
		}
	}

	private static void parseRest(XmlSerializer xmlSerializer, Rest rest) throws XmlPullParserException, IOException, NumberFormatException {

		// Start Rest tag
		xmlSerializer.startTag(null, Consts.XML_TAG_REST);

		// Start name tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Set name value
		xmlSerializer.text(rest.getName());

		// End name tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Start comment tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Set comment value
		xmlSerializer.text(rest.getComment());

		// End comment tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Start sound tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Set sound value
		xmlSerializer.text(rest.getSound());

		// End sound tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Start time tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_TIME);

		// Set time value
		xmlSerializer.text(String.valueOf(rest.getTime()));

		// End time tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_TIME);

		// End Rest tag
		xmlSerializer.endTag(null, Consts.XML_TAG_REST);
	}

	private static void parseRepetitionExercise(XmlSerializer xmlSerializer, RepetitionExercise repEx) throws XmlPullParserException, IOException {

		// Start RepetitionExercise tag
		xmlSerializer.startTag(null, Consts.XML_TAG_REPETITION_EXERCISE);

		// Start name tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Set name value
		xmlSerializer.text(repEx.getName());

		// End name tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Start comment tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Set comment value
		xmlSerializer.text(repEx.getComment());

		// End comment tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Start sound tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Set sound value
		xmlSerializer.text(repEx.getSound());

		// End sound tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Start data tag
		xmlSerializer.startTag(null, Consts.XML_TAG_REPETITION_EXERCISE_DATA);

		// Set reps
		for (int rep : repEx.getReps()) {
			// Start reps tag
			xmlSerializer.startTag(null, Consts.XML_TAG_REPETITION_EXERCISE_REPS);

			// Set number of reps
			xmlSerializer.text(String.valueOf(rep));

			// End reps tag
			xmlSerializer.endTag(null, Consts.XML_TAG_REPETITION_EXERCISE_REPS);
		}

		// Set weights
		for (double weight : repEx.getWeights()) {
			// Start weights tag
			xmlSerializer.startTag(null, Consts.XML_TAG_REPETITION_EXERCISE_WEIGHTS);

			// Set weights value
			xmlSerializer.text(String.valueOf(weight));

			// End weights tag
			xmlSerializer.endTag(null, Consts.XML_TAG_REPETITION_EXERCISE_WEIGHTS);
		}

		// End data tag
		xmlSerializer.endTag(null, Consts.XML_TAG_REPETITION_EXERCISE_DATA);

		// End RepetitionExercise tag
		xmlSerializer.endTag(null, Consts.XML_TAG_REPETITION_EXERCISE);
	}

	private static void parseTimeExercise(XmlSerializer xmlSerializer, TimeExercise timeEx) throws XmlPullParserException, IOException, NumberFormatException {

		// Start TimeExercise tag
		xmlSerializer.startTag(null, Consts.XML_TAG_TIME_EXERCISE);

		// Start name tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Set name value
		xmlSerializer.text(timeEx.getName());

		// End name tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_NAME);

		// Start comment tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Set comment value
		xmlSerializer.text(timeEx.getComment());

		// End comment tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_COMMENT);

		// Start sound tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Set sound value
		xmlSerializer.text(timeEx.getSound());

		// End sound tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_SOUND);

		// Start time tag
		xmlSerializer.startTag(null, Consts.XML_TAG_ELEMENT_TIME);

		// Set time value
		xmlSerializer.text(String.valueOf(timeEx.getTime()));

		// End time tag
		xmlSerializer.endTag(null, Consts.XML_TAG_ELEMENT_TIME);

		// End TimeExercise tag
		xmlSerializer.endTag(null, Consts.XML_TAG_TIME_EXERCISE);
	}

	private XMLWorkoutWriter() {
	}
}