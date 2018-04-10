package com.kusobotmaker.Form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import com.kusobotmaker.Data.DataAccountMode;

import lombok.Data;

@Data
public class ModesForm  implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public ModesForm(List<DataAccountMode> modes) {
		super();
		this.modes = modes;
	}
	public ModesForm() {
		super();
	}
	@Valid
	private List<DataAccountMode> modes ;
}