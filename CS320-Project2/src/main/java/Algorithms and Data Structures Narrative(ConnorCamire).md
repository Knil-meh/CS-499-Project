

For this enhancement, much like the first and will be the next enhancement, I have chosen the CS320 Contact Management Service unit testing assignment to enhance. For this assignment, I took the baseline algorithmic data structures that existed within it via the hash-based map for ID lookups and enhanced them by adding new structures and choices.

The enhancement focused on a realistic limitation of a single hash map, which was keyed by contact ID. ID-based retrieval is efficient, but searching for a partial last name would otherwise require scanning an entire stored list. To address that, I added a secondary index keyed by last name that supports the prefix-based searching, using an ordered map and range query over the IDs.

The service keeps a hash map from the contact ID to contact. The choice matches the problem where IDs are unique keys and the hash map gives it expected average time constant lookup, insert, and delete by ID. This is the backbone for CRUD, add, get, update, delete, without scanning the whole collection when you already know the ID.

Each last name of a set of contact IDs are mapped so that when a username is stored, it is stored in a tree map, which keeps last names sorted so that ordering matters for the search.allowing you to search by last name initially, and then ID in the case of multiple last names that are shared.

The prefix search takes a string, like SM, and returns every constant whose last name starts with that prefix. This implementation uses a range query on assorted keys roughly from this prefix up to just before the next prefix that would not match, then loads each of the matching contacts by ID from the main map and sorts the final list for stable, predictable output.

There is additional memory maintenance and complexity, keeping two structures synchronized. In exchange, we get better search behavior and options for non-keyed query patterns.

The unit tests were also expanded to cover the prefix matching, which were updated. Updates that changed indexed keys, deletions, multiple contracts, state sharing the same last name, and rejection of invalid search inputs.

