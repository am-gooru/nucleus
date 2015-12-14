package org.gooru.nucleus.entity;

/**
 * @author Sachin
 * 
 *         Holds type of questions supported
 */
public enum QuestionType {
  multiple_choice("multiple_choice"), multiple_answer("multiple_answer"), true_false("true_false"), fill_in_the_blank("fill_in_the_blank"),
  open_ended("open_ended"), hot_text_reorder("hot_text_reorder"), hot_text_highlight("hot_text_highlight"), hot_spot_image("hot_spot_image"),
  hot_spot_text("hot_spot_text");

  private QuestionType(String name) {

  }
}
