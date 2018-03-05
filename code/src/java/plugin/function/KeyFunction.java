/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.function;

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Identified;

/**
 * Returns the KEY for an Identified object (includes CDOMObjects)
 */
public class KeyFunction implements Function
{

	@Override
	public String getFunctionName()
	{
		return "key";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[0].jjtAccept(visitor, null);
	}

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		if (args.length != 1)
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 1 got " + args.length
				+ " " + Arrays.asList(args));
			return null;
		}
		FormatManager<?> objClass = (FormatManager<?>) args[0].jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, Optional.empty()));
		if (!semantics.isValid())
		{
			return null;
		}
		if (Identified.class.isAssignableFrom(objClass.getManagedClass()))
		{
			return FormatUtilities.STRING_MANAGER;
		}
		else
		{
			semantics.setInvalid("Parse Error: Invalid Object Format: " + objClass
				+ " is not capable of being identified");
			return null;
		}
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		Identified result = (Identified) args[0].jjtAccept(visitor, manager);
		return result.getKeyName();
	}

	@Override
	public FormatManager<?> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		args[0].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, Optional.empty()));
		return FormatUtilities.STRING_MANAGER;
	}

}