package de.adrodoc55.bio.primer.suche;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.common.gui.Highlight;

public class SequenceMatcher {

	private static final Pattern ILLEGAL_CHARACTER = Pattern.compile("[^atcg\\s\\d]");
	private static final String WD = "\\s|\\d";

	public static String getComplementary(String sequence) {
		sequence = sequence.toLowerCase();
		char[] oldChars = { 'a', 'c', 'g', 't' };
		char[] newChars = { 't', 'g', 'c', 'a' };
		sequence = CommonUtils.multiReplace(sequence, oldChars, newChars);
//		sequence = sequence.replace('a', '1').replace('t', '2').replace('g', '3').replace('c', '4');
//		sequence = sequence.replace('1', 't').replace('2', 'a').replace('3', 'c').replace('4', 'g');
		return sequence;
	}

	private static int convertToRealIndex(int index, String original) {
		int realIndex = 0;
		for (int x = 0; x < index; x++) {
			char c = original.charAt(realIndex);
			if (Pattern.matches(WD, String.valueOf(c))) {
				x--;
			}
			realIndex++;
		}
		return realIndex;
	}

	public static List<Highlight> getMatches(String origSequence, String primer) {
		return getMatches(origSequence, primer, Color.YELLOW);
	}

	public static List<Highlight> getMatches(String origSequence, String primer, Color color) {
		origSequence = origSequence.toLowerCase();
		primer = primer.toLowerCase();

		Matcher seqMatcher = ILLEGAL_CHARACTER.matcher(origSequence);

		List<Integer> seqIndecies = new ArrayList<Integer>();
		while (seqMatcher.find()) {
			seqIndecies.add(seqMatcher.start());
		}
		if (!seqIndecies.isEmpty()) {
			throw new IllegalSequenceException(seqIndecies);
		}

		Matcher primerMatcher = ILLEGAL_CHARACTER.matcher(primer);
		List<Integer> primerIndecies = new ArrayList<Integer>();
		while (primerMatcher.find()) {
			primerIndecies.add(primerMatcher.start());
		}
		if (!primerIndecies.isEmpty()) {
			throw new IllegalPrimerException(primerIndecies);
		}

		String sequence = origSequence.replaceAll(WD, "");
		primer = primer.replaceAll(WD, "");

		if (sequence.length() < primer.length()) {
			throw new IllegalArgumentException("Die Sequenz muss länger sein, als der Primer!");
		}
		List<Highlight> list = new ArrayList<Highlight>();
		if (primer.isEmpty()) {
			return list;
		}

		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(color);

		for (String seqStart = sequence.substring(0, primer.length()); !seqStart.isEmpty(); seqStart = seqStart
				.substring(0, seqStart.length() - 1)) {
			if (primer.endsWith(seqStart)) {
				int length = convertToRealIndex(seqStart.length(), origSequence);
				list.add(new Highlight(0, length, painter));
				break;
			}
		}

		for (String seqEnd = sequence.substring(sequence.length() - primer.length()); !seqEnd.isEmpty(); seqEnd = seqEnd
				.substring(1)) {
			if (primer.startsWith(seqEnd)) {
				int start = convertToRealIndex(sequence.length() - seqEnd.length(), origSequence);
				int end = convertToRealIndex(sequence.length(), origSequence);
				list.add(new Highlight(start, end, painter));
				break;
			}
		}

		Pattern pattern = Pattern.compile(primer);
		Matcher matcher = pattern.matcher(sequence);
		while (matcher.find()) {
			int start = convertToRealIndex(matcher.start(), origSequence);
			int end = convertToRealIndex(matcher.end(), origSequence);

			Highlight h = new Highlight(start, end, painter);
			list.add(h);
		}

		return list;

	}

	public static abstract class IllegalCharacterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private List<Integer> indecies;

		public IllegalCharacterException(List<Integer> indecies) {
			super("Unerlaubte Zeichen gefunden!");
			this.indecies = indecies;
		}

		public List<Integer> getIndecies() {
			return indecies;
		}
	}

	public static class IllegalSequenceException extends IllegalCharacterException {

		private static final long serialVersionUID = 1L;

		public IllegalSequenceException(List<Integer> indecies) {
			super(indecies);
		}

	}

	public static class IllegalPrimerException extends IllegalCharacterException {

		private static final long serialVersionUID = 1L;

		public IllegalPrimerException(List<Integer> indecies) {
			super(indecies);
		}

	}
}
