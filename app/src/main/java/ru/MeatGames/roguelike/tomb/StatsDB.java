package ru.MeatGames.roguelike.tomb;

/**
 * ����� ���� ������ ������������� ������.
 */

public class StatsDB {
	/**
	 * @uml.property  name="a"
	 */
	public int a; // ��������
	public String n;
	/**
	 * @uml.property  name="s"
	 */
	public boolean s; // true ���� �������� �������� (������, �������� � �.�.)
	/**
	 * @uml.property  name="m"
	 */
	public boolean m; // true ���� �������� �����������/���������� (��������, ���� � �.�.), ����� n <= n + 1
	// s � m == false ������, ��� ��� �������������� �������� ���������� ��� �������������� � ���������� �������
}
