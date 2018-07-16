package org.mcraft.kantanmemory.kernel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mcraft.kantanmemory.kernel.data.FamiliarType;
import org.mcraft.kantanmemory.kernel.data.LearningList;
import org.mcraft.kantanmemory.kernel.data.LearningWordData;
import org.mcraft.kantanmemory.kernel.data.UserWordData;

public class LearningProcess {
	private LinkedList<UserWordData> newWordList = new LinkedList<UserWordData>();
	private LinkedList<UserWordData> finishedWordList = new LinkedList<UserWordData>();

	private Deque<LearningWordData> learningWordQueue = new ArrayDeque<LearningWordData>();
	private int maximumWordQueueLength;

	private boolean isTerminated;

	public LearningProcess(LearningList learningList) {
		setMaximumWordQueueLength(20);

		newWordList.addAll(learningList.getRevisionList());
		newWordList.addAll(learningList.getNewWordList()); // new words at the back

		isTerminated = false;

		addNewWordToQueue(); // Add the first word to queue
	}

	public LearningProcess(LearningList learningList, int maximumWordQueueLength) {
		setMaximumWordQueueLength(maximumWordQueueLength);

		newWordList.addAll(learningList.getRevisionList());
		newWordList.addAll(learningList.getNewWordList()); // new words at the back

		isTerminated = false;

		addNewWordToQueue(); // Add the first word to queue
	}

	public void proceed(boolean isFamiliar) {
		if (isTerminated) {
			return;
		}

		boolean isCurrentExist = handleCurrent(isFamiliar);
		if (!isCurrentExist) {
			addNewWordToQueue();
		}

		boolean isNextExist = handleNext();
		if (!isNextExist) {
			this.isTerminated = true;
		}
	}

	/**
	 * 
	 * @param isFamiliar
	 * @return false if there is no current word
	 */
	private boolean handleCurrent(boolean isFamiliar) {
		// TODO

		LearningWordData currentWordData = getCurrentWordData();

		if (currentWordData == null) {
			return false;
		}

		currentWordData.updateLastSeenTime();

		if (isFamiliar) {
			switch (currentWordData.getFamiliarType()) {
			case FAMILIAR:
			case HALF_FAMILIAR:
				currentWordData.setFamiliarType(FamiliarType.FAMILIAR);
				currentWordData.upgradeFamiliarity();
				moveWordToFinished();
				break;
			case UNFAMILIAR:
				currentWordData.setFamiliarType(FamiliarType.HALF_FAMILIAR);
				moveWordToQueueBack();
				break;
			default:
				break;

			}
		} else {
			currentWordData.setFamiliarType(FamiliarType.UNFAMILIAR);
			currentWordData.downgradeFamiliarity();
			moveWordToQueueBack();
		}
		return true;
	}

	private void moveWordToFinished() {
		finishedWordList.add(learningWordQueue.remove());
		// TODO
	}

	private void moveWordToQueueBack() {
		learningWordQueue.add(learningWordQueue.remove());
		// TODO
	}

	/**
	 * 
	 * @return false if there is no next word
	 */
	private boolean handleNext() {
		if (newWordList.isEmpty() && learningWordQueue.isEmpty()) {
			return false;
		}

		if ((!newWordList.isEmpty()) && learningWordQueue.size() < maximumWordQueueLength) {
			addNewWordToQueue();
		}

		return true;
	}

	/**
	 * Add word from new word list to front of the queue.
	 */
	private boolean addNewWordToQueue() {
		if (!newWordList.isEmpty()) {
			learningWordQueue.addFirst(new LearningWordData(newWordList.pop()));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add word to front of the queue.
	 */
	private void addNewWordToQueue(LearningWordData wordData) {
		learningWordQueue.addFirst(wordData);
	}

	public EnumMap<FamiliarType, Integer> getNumbersOfTypes() {
		// TODO
		Map<FamiliarType, Integer> numOfTypesMap = new HashMap<FamiliarType, Integer>();
		numOfTypesMap.put(FamiliarType.UNFAMILIAR, 0);
		numOfTypesMap.put(FamiliarType.HALF_FAMILIAR, 0);
		numOfTypesMap.put(FamiliarType.FAMILIAR, 0);

		for (LearningWordData wordData : learningWordQueue) {
			switch (wordData.getFamiliarType()) {
			case FAMILIAR:
				numOfTypesMap.put(FamiliarType.FAMILIAR, numOfTypesMap.get(FamiliarType.FAMILIAR) + 1);
				break;
			case HALF_FAMILIAR:
				numOfTypesMap.put(FamiliarType.HALF_FAMILIAR, numOfTypesMap.get(FamiliarType.HALF_FAMILIAR) + 1);
				break;
			case UNFAMILIAR:
				numOfTypesMap.put(FamiliarType.UNFAMILIAR, numOfTypesMap.get(FamiliarType.UNFAMILIAR) + 1);
				break;
			default:
				break;
			}
		}
		return new EnumMap<FamiliarType, Integer>(numOfTypesMap);
	}

	public LinkedList<UserWordData> getNewWordList() {
		return newWordList;
	}

	public void setNewWordList(LinkedList<UserWordData> newWordList) {
		this.newWordList = newWordList;
	}

	public LinkedList<UserWordData> getFinishedWordList() {
		return finishedWordList;
	}

	public void setFinishedWordList(LinkedList<UserWordData> finishedWordList) {
		this.finishedWordList = finishedWordList;
	}

	public Deque<LearningWordData> getLearningWordQueue() {
		return learningWordQueue;
	}

	public void setLearningWordQueue(Deque<LearningWordData> learningWordQueue) {
		this.learningWordQueue = learningWordQueue;
	}

	public List<UserWordData> getAllWords() {
		List<UserWordData> list = new ArrayList<UserWordData>();
		list.addAll(newWordList);
		list.addAll(learningWordQueue);
		list.addAll(finishedWordList);
		return list;
	}

	public int getMaximumWordQueueLength() {
		return maximumWordQueueLength;
	}

	public void setMaximumWordQueueLength(int maximumWordQueueLength) {
		this.maximumWordQueueLength = maximumWordQueueLength;
	}

	public LearningWordData getCurrentWordData() {
		return learningWordQueue.peek();
	}

	public void setCurrentWordData(LearningWordData currentWordData) {
		addNewWordToQueue(currentWordData);
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void setTerminated(boolean isTerminated) {
		this.isTerminated = isTerminated;
	}

}
