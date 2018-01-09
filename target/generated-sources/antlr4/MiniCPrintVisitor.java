


public class MiniCPrintVisitor extends MiniCBaseVisitor<String>{
	int compoundNum;
	
	boolean isBinaryOperation(MiniCParser.ExprContext ctx) {
		// expr의 child개수가 3일 때 호출되며 이항 연산을 확인한다.
			return ctx.getChild(1) != ctx.expr();
	}
	
	boolean isFunctionExpr(MiniCParser.ExprContext ctx) {
		// expr의 child개수가 4일 때만 호출되므로 functions인지 배열인지 확인하는 함수
		return ctx.getChild(1).getText().equals("(");
	}
	
	public String howManyTabs(int curDepth, int loopDepth){
		// tree의 깊이만큼 tab을 해주는 함수
		String s = "";
		
		for(int i = 0; i < (curDepth-(2*compoundNum+2))/loopDepth; i++){
			s += "....";
		}
		return s;
	}

	@Override
	public String visitProgram(MiniCParser.ProgramContext ctx) {
		// TODO Auto-generated method stub
		// program	: decl+
		String s = "";
		
		for(int i = 0; i < ctx.getChildCount(); i++){
			s += visit(ctx.decl(i)) + "\n";
		}
		System.out.println(s);
		return s;
	}

	@Override 
	public String visitDecl(MiniCParser.DeclContext ctx) {
		// TODO Auto-generated method stub
		// decl	: var_decl | fun_decl
		String s1 = "";
		
		if(ctx.getChild(0) instanceof MiniCParser.Fun_declContext) {
			// instanceof를 이용한 클래스 타입비교
			s1 = visit(ctx.fun_decl());
		}
		else {
			s1 = visit(ctx.var_decl());
		}
		return s1; 
   }

	@Override
	public String visitVar_decl(MiniCParser.Var_declContext ctx) {
		// TODO Auto-generated method stub
		// var_decl	:  type_spec IDENT ';' | type_spec IDENT '=' LITERAL ';'| type_spec IDENT '[' LITERAL ']' ';'
		String s1 = "", s2 = "", s3 = "";
		s1 = visit(ctx.type_spec());
		s2 = ctx.IDENT().getText();
		
		switch(ctx.getChild(2).getText()){
		case ";":
			return s1 + " " + s2 + ";";
		case "=":
			s3 = ctx.LITERAL().getText();
			return s1 + " " + s2 + " = " + s3 + ";";
		case "[":
			s3 = ctx.LITERAL().getText();
			return s1 + " " + s2 + "[" + s3 + "];";
		default :
			return super.visitVar_decl(ctx);
		}
	}

	@Override
	public String visitType_spec(MiniCParser.Type_specContext ctx) {
		// TODO Auto-generated method stub
		// type_spec : VOID | INT
		return ctx.getChildCount() == 1 && ctx.getChild(0).getText().equals("void") ? "void" : "int";
		// getChild만으론 String형으로 나오지않기때문에 getText를 해주고 비교해야한다.
	}

	@Override
	public String visitFun_decl(MiniCParser.Fun_declContext ctx) {
		// TODO Auto-generated method stub
		// fun_decl	: type_spec IDENT '(' params ')' compound_stmt
		String s1 = "", s2 = "", s3 = "", s4 = "";
		
		s1 = visit(ctx.type_spec());
		s2 = ctx.IDENT().getText();
		s3 = visit(ctx.params());
		s4 = visit(ctx.compound_stmt());
		
		return s1 + " " + s2 + " (" + s3 + ")\n" + s4;
	}

	@Override
	public String visitParams(MiniCParser.ParamsContext ctx) {
		// TODO Auto-generated method stub
		// params : param (',' param)*| VOID |	
		String s = "";
		if(ctx.getChildCount() == 0){
			return "";
		}
		else if(ctx.getChild(0).getText().equals("void")){
			return visit(ctx.VOID());
		}
		else{
			s = visit(ctx.param(0));
			for(int i = 1; i < ctx.param().size(); i++)
				s += ", " + visit(ctx.param(i));
			return s;
		}
	}

	@Override
	public String visitParam(MiniCParser.ParamContext ctx) {
		// TODO Auto-generated method stub
		// param : type_spec IDENT | type_spec IDENT '[' ']'
		String s1 = "", s2  = "";
		
		s1 = visit(ctx.type_spec());
		s2 = ctx.IDENT().getText();
		
		if(ctx.getChild(2).getText().equals("["))
			return s1 + " " + s2 + "[]";
		else
			return s1 + " " + s2;
	}

	@Override
	public String visitStmt(MiniCParser.StmtContext ctx) {
		// TODO Auto-generated method stub
		// stmt	: expr_stmt	| compound_stmt	| if_stmt | while_stmt | return_stmt
		if(ctx.getChild(0) instanceof MiniCParser.Expr_stmtContext){
			if(ctx.parent instanceof MiniCParser.If_stmtContext || ctx.parent instanceof MiniCParser.While_stmtContext){
				String st = "", sp = "", sr = "";
				
				st = howManyTabs(ctx.depth(), 2);
				sp = st + "{\n";
				sr = st + "}";
				return sp + st + "...." + visit(ctx.expr_stmt()) + "\n" + sr;
			}
			return visit(ctx.expr_stmt());
		}
		else if(ctx.getChild(0) instanceof MiniCParser.Compound_stmtContext)
			return visit(ctx.compound_stmt());
		else if(ctx.getChild(0) instanceof MiniCParser.If_stmtContext){
			if(ctx.parent instanceof MiniCParser.If_stmtContext || ctx.parent instanceof MiniCParser.While_stmtContext){
				String st = "", sp = "", sr = "";
				
				st = howManyTabs(ctx.depth(), 2);
				sp = st + "{\n";
				sr = st + "}";
				return sp + st + "...." + visit(ctx.if_stmt()) + "\n" + sr;
			}
			return visit(ctx.if_stmt());
		}
		else if(ctx.getChild(0) instanceof MiniCParser.While_stmtContext){

			if(ctx.parent instanceof MiniCParser.If_stmtContext || ctx.parent instanceof MiniCParser.While_stmtContext){
				String st = "", sp = "", sr = "";
				
				st = howManyTabs(ctx.depth(), 2);
				sp = st + "{\n";
				sr = st + "}";
				return sp + st + "...." + visit(ctx.while_stmt()) + "\n" + sr;
			}
			return visit(ctx.while_stmt());
		}
		else
			return visit(ctx.return_stmt());
	}

	@Override
	public String visitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
		// TODO Auto-generated method stub
		// expr_stmt : expr ';'
		return visit(ctx.expr()) + ";";
	}

	@Override
	public String visitWhile_stmt(MiniCParser.While_stmtContext ctx) {
		// TODO Auto-generated method stub
		// while_stmt : WHILE '(' expr ')' stmt
		String s1 = "", s2 = "", s3 = "";
		
		s1 = ctx.WHILE().getText();
		s2 = visit(ctx.expr());
		s3 = visit(ctx.stmt());
		
		return s1 + " (" + s2 + ")\n" + s3;
	}

	@Override
	public String visitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
		// TODO Auto-generated method stub
		// compound_stmt: '{' local_decl* stmt* '}'
		String s1 = "", s2 = "", sp = "", sr = "", st = "";
		compoundNum++;
		st = howManyTabs(ctx.depth(), 2);
		sp = st + "{\n";
		sr = st + "}";
		
		for(int i = 0; i < ctx.local_decl().size(); i++){
			s1 += st + "...." + visit(ctx.local_decl(i)) + "\n";
		}
		for(int i = 0; i < ctx.stmt().size(); i++){
			s2 += st + "...." + visit(ctx.stmt(i)) + "\n";
		}
		compoundNum--;
		return sp + s1 + s2 + sr;
	}

	@Override
	public String visitLocal_decl(MiniCParser.Local_declContext ctx) {
		// TODO Auto-generated method stub
		// local_decl	: type_spec IDENT ';'| type_spec IDENT '=' LITERAL ';'	| type_spec IDENT '[' LITERAL ']' ';'
		String s1 = "", s2 = "", s3 = "";
		s1 = visit(ctx.type_spec());
		s2 = ctx.IDENT().getText();
		
		switch(ctx.getChild(2).getText()){
		case ";":
			return s1 + " " + s2 + ";";
		case "=":
			s3 = ctx.LITERAL().getText();
			return s1 + " " + s2 + " = " + s3 + ";";
		case "[":
			s3 = ctx.LITERAL().getText();
			return s1 + " " + s2 + "[" + s3 + "];";
		default :
			return super.visitLocal_decl(ctx);
		}
	}
	
	@Override
	public String visitIf_stmt(MiniCParser.If_stmtContext ctx) {
		// TODO Auto-generated method stub
		// if_stmt	: IF '(' expr ')' stmt	| IF '(' expr ')' stmt ELSE stmt
		String s1 = "", s2 = "", s3 = "", s4 = "", s5 = "";
		
		s1 = ctx.IF().getText();
		s2 = visit(ctx.expr());
		s3 = visit(ctx.stmt(0));
		
		if(ctx.getChildCount() == 5){
			return s1 +" (" + s2 + ")\n"
					+ s3;
		}
		else{
			String st = "", sp = "", sr = "";
			s4 = ctx.ELSE().getText();
			s5 = visit(ctx.stmt(1));
			st = howManyTabs(ctx.depth(),  2);
			if(ctx.parent instanceof MiniCParser.If_stmtContext || ctx.parent instanceof MiniCParser.While_stmtContext){
				System.out.println(st);
				sp = st + "{\n";
				sr = st + "}";
				return s1 +" (" + s2 + ")\n"
				+ s3 + "\n"
				+ st + s4 + "\n"
				+ sp + s5
				+ sr;
			}
			return s1 +" (" + s2 + ")\n"
			+ s3 + "\n"
			+ st + s4 + "\n"
			+ s5;
		}
	}

	@Override
	public String visitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
		// TODO Auto-generated method stub
		// return_stmt	: RETURN ';'| RETURN expr ';'
		String s1 = "", s2 = "";
		
		s1 = ctx.RETURN().getText();
		if(ctx.getChildCount() == 2)
			return s1 + ";";
		else{
			s2 = visit(ctx.expr());
			
			return s1 + " " + s2 + ";";
		}	
	}

	@Override
	public String visitExpr(MiniCParser.ExprContext ctx) {
		// TODO Auto-generated method stub
		/*expr	:  
		case1'(LITERAL|IDENT)											
		case3' | '(' expr ')'				 							
		case4' | IDENT '[' expr ']'			 						
		case4' | IDENT '(' args ')'									
		case2' | op=('-'|'+'|'--'|'++'|'!') expr								
		case3' | left=expr op=('*'|'/'|'%'|'+'|'-') right=expr			 
		case3' | left=expr op=(EQ|NE|LE|'<'|GE|'>'|AND|OR) right=expr		
		case3' | IDENT '=' expr										
		case6' | IDENT '[' expr ']' '=' expr*/
		  String s1 = "", s2 = "", s3 = "", op = ""; 
		  
		  switch(ctx.getChildCount()){
		  case 1:
			  return ctx.getChild(0).getText();
		  case 2:
			  s1 = visit(ctx.expr(0));
			  op = ctx.getChild(0).getText();
			  return op + s1;
		  case 3:
			  if (isBinaryOperation(ctx)) {
				  if(ctx.getChild(1).getText().equals("=")){
					  s1 = ctx.IDENT().getText();
					  s2 = visit(ctx.expr(0));
					  return s1 + " = " + s2;
				  }
				  else{
					  s1 = visit(ctx.expr(0)); 
					  s2 = visit(ctx.expr(1));
					  op = ctx.getChild(1).getText();  
					  return s1 + " " + op + " " + s2;   
				  }
			  }else{
				  s1 = visit(ctx.expr(0));
				  return "(" + s1 + ")";
			  }
		  case 4:
			  s1 = ctx.IDENT().getText();
			  if(isFunctionExpr(ctx)){
				  s2 = visit(ctx.args());
				  return s1 + "(" + s2 + ")";
			  }
			  else{
				  s2 = visit(ctx.expr(0));
				  return s1 + "[" + s2 + "]";
			  }
		  case 6:
			  s1 = ctx.IDENT().getText();
			  s2 = visit(ctx.expr(0));
			  s3 = visit(ctx.expr(1));
			  
			  return s1 + "[" + s2 + "] = " + s3;
		  }
		  
		return super.visitExpr(ctx);
	}

	@Override
	public String visitArgs(MiniCParser.ArgsContext ctx) {
		// TODO Auto-generated method stub
		// args	: expr (',' expr)* | 
		String s = "";
		
		if(ctx.getChild(0).equals("")){
			return "";
		}
		else{
			s = visit(ctx.expr(0));
			for(int i = 1; i < ctx.expr().size(); i++)
				s += ", " + visit(ctx.expr(i));
			return s;
		}
	}
	
	

	
}
