package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WhenInstruction extends Instruction {

	String whenSyntax = "when \"(?<expression>.*?)\"";
	String applicationWithConditonInExpressionSyntax = "(?<application>.*?) terminates condition (?<condition>.*?)";
	String applicationInExpressionSyntax = "(?<application>.*?) terminates";
	String eventWithConditonInExpressionSyntax = "(?<event>.*?) receives condition (?<condition>.*?)";
	String eventInExpressionSyntax = "(?<event>.*?) receives";
	String applicationInExpressionFailsSyntax = "(?<application>.*?) fails";

	public enum State{
		TERMINATES,
		RECEIVES,
		FAILS
	}

	public void setApplicationsOrEvents(List<ApplicationOrEventInExpression> applicationsOrEvents) {
		this.applicationsOrEvents = applicationsOrEvents;
	}

	public void setAggregationExpression(String aggregationExpression) {
		this.aggregationExpression = aggregationExpression;
	}

	public class ApplicationOrEventInExpression{

		State state;
		String name;
		String condition;
		int order;

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCondition() {
			return condition;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		@Override
		public String toString() {
			return "Application [state=" + state + ", name=" + name + ", condition=" + condition + ", order=" + order
					+ "]";
		}		
	}

	int indexInInInstruction; 
	
	int numberOfBrackets;
	List<ApplicationOrEventInExpression> applicationsOrEvents = new ArrayList<ApplicationOrEventInExpression>();
	int applicationEventOrder;
	String aggregationExpression = "";

	public WhenInstruction(){
		super("when");
	}

	public WhenInstruction(String whenInstruction, int lineNumber){
		super(whenInstruction.trim(), lineNumber, "when");
	}

	public WhenInstruction(String whenInstruction) {
		super(whenInstruction.trim(), "when");
	}

	public void analysis() throws OrchaCompilationException {

		indexInInInstruction = 0;

		Pattern pattern = Pattern.compile(whenSyntax);

		Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
		if(result.matches() == false) {
			throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
		}

		String expression = result.group("expression");

		expression = expression.replaceAll("\t", " ");

		numberOfBrackets = 0;
		applicationEventOrder = 1;

		analysis(expression);

		if(numberOfBrackets != 0) {
			throw new OrchaCompilationException("Brackets error in expression.");
		}

	}

	public int getIndexInInInstruction() {
		return indexInInInstruction;
	}

	public int getNumberOfBrackets() {
		return numberOfBrackets;
	}

	private ApplicationOrEventInExpression extractApplicationFromExpression(String expression) throws OrchaCompilationException {

		ApplicationOrEventInExpression applicationOrEvent = new ApplicationOrEventInExpression();
		
		Pattern pattern = Pattern.compile(applicationWithConditonInExpressionSyntax);
		Matcher result = pattern.matcher(expression);
		if(result.matches() == true) {
			applicationOrEvent.state = State.TERMINATES;
			applicationOrEvent.name = result.group("application");
			applicationOrEvent.condition = result.group("condition");
			return applicationOrEvent;
		}
		
		pattern = Pattern.compile(applicationInExpressionSyntax);
		result = pattern.matcher(expression);
		if(result.matches() == true) {
			applicationOrEvent.state = State.TERMINATES;
			applicationOrEvent.name = result.group("application");
			applicationOrEvent.condition = null;
			return applicationOrEvent;
		}

		pattern = Pattern.compile(eventWithConditonInExpressionSyntax);
		result = pattern.matcher(expression);
		if(result.matches() == true) {
			applicationOrEvent.state = State.RECEIVES;
			applicationOrEvent.name = result.group("event");
			applicationOrEvent.condition = result.group("condition");
			return applicationOrEvent;
		}

		pattern = Pattern.compile(eventInExpressionSyntax);
		result = pattern.matcher(expression);
		if(result.matches() == true) {
			applicationOrEvent.state = State.RECEIVES;
			applicationOrEvent.name = result.group("event");
			applicationOrEvent.condition = null;
			return applicationOrEvent;
		}

		pattern = Pattern.compile(applicationInExpressionFailsSyntax);
		result = pattern.matcher(expression);
		if(result.matches() == true) {
			applicationOrEvent.state = State.FAILS;
			applicationOrEvent.name = result.group("application");
			applicationOrEvent.condition = null;
			return applicationOrEvent;
		}
		
		throw new OrchaCompilationException("Error in expression: " + expression);

	}

	private void analysis(String expression) throws OrchaCompilationException {
		
		while(indexInInInstruction<expression.length() && Character.isWhitespace(expression.charAt(indexInInInstruction))) {
			indexInInInstruction++;
		}

		expression = expression.trim();

		if(expression.startsWith("(")){
			
			aggregationExpression = aggregationExpression + "(";
			this.numberOfBrackets++;
			indexInInInstruction++;
			analysis(expression.substring(1));
			
		} else {
			
			int endIndexOfSubExpression = expression.indexOf(")");
			
			// there is a closing bracket
			if(endIndexOfSubExpression != -1) {
				this.numberOfBrackets--;
				if(numberOfBrackets < 0) {
					throw new OrchaCompilationException("Too much closing brackets in expression.");
				}
				String s = expression.substring(0, endIndexOfSubExpression);
				s = s.trim();
				ApplicationOrEventInExpression application = this.extractApplicationFromExpression(s);
				application.order = applicationEventOrder;
				applicationEventOrder++;
				aggregationExpression = aggregationExpression + this.convert(application) + ")";
				this.applicationsOrEvents.add(application);

				// there is another opening bracket after the closing one
				int beginIndexOfNextExpression = expression.indexOf("(");				
				if(beginIndexOfNextExpression != -1) {
					endIndexOfSubExpression++;

					// skip white spaces
					while(endIndexOfSubExpression<expression.length() && Character.isWhitespace(expression.charAt(endIndexOfSubExpression))) {
						endIndexOfSubExpression++;
					}
					
					// while closing brackets
					while(endIndexOfSubExpression<expression.length() && expression.charAt(endIndexOfSubExpression) == ')') {
						endIndexOfSubExpression++;
						while(endIndexOfSubExpression<expression.length() && Character.isWhitespace(expression.charAt(endIndexOfSubExpression))) {
							endIndexOfSubExpression++;
						}
						aggregationExpression = aggregationExpression + ")";						
						this.numberOfBrackets--;
					}

					this.numberOfBrackets++;
					String operator = expression.substring(endIndexOfSubExpression, beginIndexOfNextExpression);
					operator = operator.trim();
					aggregationExpression = aggregationExpression + " " + this.logicalOperator(operator) + " (";
					
					indexInInInstruction = indexInInInstruction + beginIndexOfNextExpression+1;
					
					analysis(expression.substring(beginIndexOfNextExpression+1));

				} else {	// there is no other opening bracket => check for all closing brackets
					
					endIndexOfSubExpression++;
					
					// skip white spaces
					while(endIndexOfSubExpression<expression.length() && Character.isWhitespace(expression.charAt(endIndexOfSubExpression))) {
						endIndexOfSubExpression++;
					}
					
					// while closing brackets
					while(endIndexOfSubExpression<expression.length() && expression.charAt(endIndexOfSubExpression) == ')') {
						endIndexOfSubExpression++;
						while(endIndexOfSubExpression<expression.length() && Character.isWhitespace(expression.charAt(endIndexOfSubExpression))) {
							endIndexOfSubExpression++;
						}
						aggregationExpression = aggregationExpression + ")";						
						this.numberOfBrackets--;
					}

				}
			} else {
				ApplicationOrEventInExpression application = this.extractApplicationFromExpression(expression);
				application.order = applicationEventOrder;
				applicationEventOrder++;
				aggregationExpression = aggregationExpression + this.convert(application);
				this.applicationsOrEvents.add(application);
			}
		}		
		
	}
	
	public List<ApplicationOrEventInExpression> getApplicationsOrEvents() {
		return applicationsOrEvents;
	}

	public String getApplicationsOrEventsAsCapitalizedConcatainedString(){
		List<String> list = applicationsOrEvents.stream().map(ApplicationOrEventInExpression::getName).collect(Collectors.toList());
		String concatainedString = "";
		for(String name: list){
			concatainedString = concatainedString + name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return concatainedString;
	}

	public String getAggregationExpression() {
		return "size()==" + applicationsOrEvents.size() + " AND (" + aggregationExpression + ")";
	}
	
	public String convert(ApplicationOrEventInExpression application) {
		return application.toString();
	}
	
	public String logicalOperator(String operator) throws OrchaCompilationException {
		return operator;
	}

	@Override
	public String toString() {
		return "WhenInstruction{" +
				"applicationWithConditonInExpressionSyntax='" + applicationWithConditonInExpressionSyntax + '\'' +
				", applicationInExpressionSyntax='" + applicationInExpressionSyntax + '\'' +
				", eventWithConditonInExpressionSyntax='" + eventWithConditonInExpressionSyntax + '\'' +
				", eventInExpressionSyntax='" + eventInExpressionSyntax + '\'' +
				", applicationInExpressionFailsSyntax='" + applicationInExpressionFailsSyntax + '\'' +
				", indexInInInstruction=" + indexInInInstruction +
				", numberOfBrackets=" + numberOfBrackets +
				", applicationsOrEvents=" + applicationsOrEvents +
				", applicationEventOrder=" + applicationEventOrder +
				", aggregationExpression='" + aggregationExpression + '\'' +
				"} " + super.toString();
	}
}
