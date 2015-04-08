/**
 * AgendaTrial1.java
 *
 * Copyright (c) 2011-2014, JFXtras
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jfxtras.labs.scene.control.gauge.linear.trial;

import java.util.List;

import javafx.scene.layout.FlowPane;
import jfxtras.labs.scene.control.gauge.linear.BasicArcGauge;
import jfxtras.labs.scene.control.gauge.linear.LinearGauge;

/**
 * @author Tom Eugelink
 */
public class BasicArcGaugeTrial1 extends LinearGaugeTrial1 {
	
    public static void main(String[] args) {
        launch(args);       
    }

    public LinearGauge<?> createLinearGauge() {
    	return new BasicArcGauge();
    }
    
	@Override
	public void addDeviatingGauges(List<LinearGauge<?>> gauges, FlowPane lFlowPane) {
        
        // dark
		{
			final LinearGauge<?> lLinearGauge = createLinearGauge();
			lLinearGauge.setStyle("-fx-border-color: #000000;");
			lLinearGauge.getStyleClass().add("colorscheme-dark");
			lFlowPane.getChildren().add(lLinearGauge);
			gauges.add(lLinearGauge);
		}
	}
}

