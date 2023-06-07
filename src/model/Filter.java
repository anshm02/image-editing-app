package model;

/**
 * Represents the different types of filters.
 * Each filter manipulates an image accordingly.
 */
public enum Filter {
  NORMAL("normal"),
  RED("red-component"),
  GREEN("green-component"),
  BLUE("blue-component"),
  BRIGHTV("brighten-value"),
  DARKENV("darken-value"),
  BRIGHTI("brighten-intensity"),
  DARKENI("darken-intensity"),
  BRIGHTL("brighten-luma"),
  DARKENL("darken-luma"),
  BLENDINGDIFF("blending-difference"),
  BLENDINGSCREEN("blending-screen"),
  BLENDINGMULTIPLY("blending-multiply");

  private final String value;
  Filter(String value) {
    this.value = value;
  }


  /**
   * Gets the value of the inputted attribute.
   * @return the value of the attribute
   */
  public String getValue() {
    return this.value;
  }

}
