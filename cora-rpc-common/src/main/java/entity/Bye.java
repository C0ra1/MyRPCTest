package entity;

import lombok.Data;

import java.io.Serializable;

//这些都是之前封装好传过去的

/**
 * @author C0ra1
 * @version 1.0
 */
@Data
public class Bye implements Serializable {
    private Integer methodID;
    private String saying;
}
